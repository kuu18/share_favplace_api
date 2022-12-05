package com.example.sharefavplace.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.sharefavplace.mapper.ToFavplaceMapper;
import com.example.sharefavplace.mapper.ToScheduleMapper;
import com.example.sharefavplace.model.Category;
import com.example.sharefavplace.model.Favplace;
import com.example.sharefavplace.model.Schedule;
import com.example.sharefavplace.model.User;
import com.example.sharefavplace.param.FavplaceParam;
import com.example.sharefavplace.param.ScheduleParam;
import com.example.sharefavplace.repository.FavplaceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class FavplaceServiceImpl implements FavplaceService {

  private final FavplaceRepository favplaceRepository;
  private final UserService userService;
  private final CategoryService categoryService;
  private final FileService fileService;
  private final ScheduleService scheduleService;
  private Map<String, Object> responseBody = new HashMap<>();

  /**
   * idによるFavplace取得（1件）
   * 
   * @param id
   * @return Favplace
   */
  @Override
  public Favplace getFavplaceById(Integer id) {
    return favplaceRepository.selectFavplacesbyId(id);
  }


  /**
   * ユーザーのFavplace一覧取得（ページネーション）
   * 
   * @param userId
   * @return List<Favplace>
   */
  @Override
  public List<Favplace> getFavplacesByUserId(Integer userId, final int pPageIndex, final int pCountPerPage) {
    return favplaceRepository.selectFavplacesbyUserId(userId, pPageIndex, pCountPerPage);
  }

  /**
   * ユーザーのFavplace総数取得
   * 
   * @param userId
   * @return Favplace数
   */
  @Override
  public Long getUsersFavplacesCount(Integer userId) {
    return favplaceRepository.getUsersFavplacesCount(userId);
  }

  /**
   * Favplaces新規登録
   * 
   * @param favplace
   * @return Favplace
   */
  @Override
  public Favplace saveFavplace(Favplace favplace) {
    return favplaceRepository.save(favplace);
  }

  /**
   * Favplaceのスケジュール更新
   * 
   * @param favplace
   * @return Favplace
   */
  @Override
  public Favplace updateSchedule(Favplace favplace) {
    return favplaceRepository.updateSchedule(favplace);
  }

  /**
   * favplace画像アップロード
   * 
   * @param image
   * @param username
   * @return imageUrl
   */
  public String uploadImage(MultipartFile image, String username) {
    LocalDateTime createAt = LocalDateTime.now();
    String s3Path = System.getenv("AWSS3_BUCKET_NAME") + "/" + username + "/favplace";
    String imageUrl = fileService.fileUpload(image, createAt, s3Path).toString();
    return imageUrl;
  }

  /**
   * favplace新規登録ロジック
   * 
   * @param favplaceparam
   * @param image
   * @return responseBody
   */
  public Map<String, Object> saveFavplace(Optional<MultipartFile> image, FavplaceParam favplaceParam, Optional<ScheduleParam> scheduleParam) {
    // ユーザー取得
    User user = userService.findById(favplaceParam.getUserId()).get();
    // カテゴリー取得
    Category category = categoryService.findById(favplaceParam.getCategoryId()).get();
    // Beanマッピング
    Favplace favplace = new Favplace();
    favplace = ToFavplaceMapper.INSTANCE.favplaceParamToFavplace(favplaceParam);
    favplace.setUser(user);
    favplace.setCategory(category);
    // 画像がある場合画像を登録
    if (image.isPresent() && image.get().getSize() != 0) {
      String imageUrl = uploadImage(image.get(), user.getUsername());
      favplace.setImageUrl(imageUrl);
    }
    // favplace新規登録
    Favplace savedFavplace = saveFavplace(favplace);
    // スケジュールがある場合スケジュールを登録
    if(scheduleParam.isPresent()) {
      Schedule schedule = new Schedule();
      schedule = scheduleParam.get().getTimed()
        ? ToScheduleMapper.INSTANCE.scheduleParamToscheduleWithTime(scheduleParam.get())
        : ToScheduleMapper.INSTANCE.scheduleParamToschedule(scheduleParam.get());
      schedule.setUser(user);
      schedule.setFavplace(savedFavplace);
      scheduleService.saveSchedule(schedule);
      savedFavplace.setSchedule(schedule);
      Favplace updatedFavplace = updateSchedule(savedFavplace);
      responseBody.put("favplace", updatedFavplace);
    } else {
      responseBody.put("favplace", savedFavplace);
    }
    responseBody.put("message", "favplaceを登録しました。");
    return responseBody;
  }

  /**
   * ユーザーのFavplaces一覧取得ロジック
   * 
   * @param userId
   * @return Map<String, Object> 
   */
  public Map<String, Object> getFavplacesByUserId(Integer userId, Integer pPageIndex) {
    responseBody.put("favplaces", getFavplacesByUserId(userId, pPageIndex, 12));
    responseBody.put("count", getUsersFavplacesCount(userId));
    return responseBody;
  }

}
