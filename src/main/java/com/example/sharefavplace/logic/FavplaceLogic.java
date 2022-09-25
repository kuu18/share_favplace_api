package com.example.sharefavplace.logic;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.sharefavplace.mapper.ToFavplaceMapper;
import com.example.sharefavplace.mapper.ToUserMapper;
import com.example.sharefavplace.model.Favplace;
import com.example.sharefavplace.model.User;
import com.example.sharefavplace.param.FavplaceParam;
import com.example.sharefavplace.service.FavplaceService;
import com.example.sharefavplace.service.FileService;
import com.example.sharefavplace.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FavplaceLogic {

  private final FavplaceService favplaceService;
  private final UserService userService;
  private final FileService fileService;

  /**
   * favplace新規登録
   * 
   * @param param
   * @return responseBody(message, favplace)
   */
  @Transactional
  public Map<String, Object> saveFavplace(MultipartFile image, FavplaceParam params) {
    User user = userService.findById(params.getUserId()).get();
    Favplace favplace = new Favplace();
    favplace = ToFavplaceMapper.INSTANCE.favplaceParamToFavplace(params);
    user = ToUserMapper.INSTANCE.toResponseUser(user);
    favplace.setUser(user);
    if (image.getSize() != 0) {
      String imageUrl = uploadImage(image, user.getUsername());
      favplace.setImageUrl(imageUrl);
    }
    Favplace savedFavplace = favplaceService.saveFavplace(favplace);
    savedFavplace = favplaceService.addCategoryToFavplaces(savedFavplace.getId(), params.getCategoryIds());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("message", "favplaceを登録しました。");
    responseBody.put("favplace", savedFavplace);
    return responseBody;
  }

  /**
   * favplace画像アップロード
   * 
   * @param image、username
   * @return imageUrl
   */
  public String uploadImage(MultipartFile image, String username) {
    try {
      LocalDateTime createAt = LocalDateTime.now();
      String s3Path = System.getenv("AWSS3_BUCKET_NAME") + "/" + username + "/favplace";
      String avatarUrl = fileService.fileUpload(image, createAt, s3Path).toString();
      return avatarUrl;
    } catch (Exception e) {
      // TODO: handle exception
      throw new RuntimeException(e.getMessage());
    }
  }

}
