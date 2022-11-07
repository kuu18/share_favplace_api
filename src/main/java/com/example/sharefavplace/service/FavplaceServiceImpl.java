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
import com.example.sharefavplace.model.Category;
import com.example.sharefavplace.model.Favplace;
import com.example.sharefavplace.model.User;
import com.example.sharefavplace.param.FavplaceParam;
import com.example.sharefavplace.repository.CategoryRepository;
import com.example.sharefavplace.repository.FavplaceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class FavplaceServiceImpl implements FavplaceService {
  
  private final FavplaceRepository favplaceRepository;
  private final CategoryRepository categoryRepository;
  private final UserService userService;
  private final FileService fileService;
  private Map<String, Object> responseBody = new HashMap<>();

  /**
   * idによるFavplace取得
   * 
   * @param id
   * @return Favplace
   */
  @Override
  public Favplace getFavplaceById(Integer id) {
    return favplaceRepository.selectFavplacebyId(id);
  }

  /**
   * user_idによるFavplaces取得
   * 
   * @param userId
   * @return List<Favplace>
   */
  @Override
  public List<Favplace> getFavplacesByUserId(Integer userId) {
    return favplaceRepository.selectAllFavplacesbyUserId(userId);
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
   * favplaceにcategoryを追加するメソッド
   * 
   * @param favplaceId
   * @param categoryId
   */
  @Override
  public Favplace addCategoryToFavplaces(Integer favplaceId, Iterable<Integer> categoryIds) {
    Favplace favplace = favplaceRepository.findById(favplaceId).get();
    Iterable<Integer> iterableCategoryIds = categoryIds;
    List<Category> categories = categoryRepository.findAllById(iterableCategoryIds);
    categories.stream().forEach(category -> favplace.getCategories().add(category));
    return favplace;
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
  public Map<String, Object> saveFavplace(Optional<MultipartFile> image, FavplaceParam params) {
    User user = userService.findById(params.getUserId()).get();
    Favplace favplace = new Favplace();
    favplace = ToFavplaceMapper.INSTANCE.favplaceParamToFavplace(params);
    favplace.setUser(user);
    if (image.isPresent() && image.get().getSize() != 0) {
      String imageUrl = uploadImage(image.get(), user.getUsername());
      favplace.setImageUrl(imageUrl);
    }
    Favplace savedFavplace = saveFavplace(favplace);
    savedFavplace = addCategoryToFavplaces(savedFavplace.getId(), params.getCategoryIds());
    responseBody.put("message", "favplaceを登録しました。");
    responseBody.put("favplace", savedFavplace);
    return responseBody;
  }

}
