package com.example.sharefavplace.api;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

import com.example.sharefavplace.model.Favplace;
import com.example.sharefavplace.param.FavplaceParam;
import com.example.sharefavplace.param.ScheduleParam;
import com.example.sharefavplace.service.FavplaceServiceImpl;
import com.example.sharefavplace.utils.ResponseUtils;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/favplaces")
@RequiredArgsConstructor
public class FavplaceResource {

  private final FavplaceServiceImpl favplaceService;

  /**
   * Favplace取得
   * @param id
   * @return Favplace
   */
  @GetMapping("/{id}")
  public ResponseEntity<Favplace> getFavplaceById(@PathVariable Integer id) {
    return ResponseEntity.ok().body(favplaceService.getFavplaceById(id));
  }

  /**
   * ユーザーのFavplace取得
   * @param id
   * @param pageIndex
   * @return ユーザーのFavplace一覧（ページネーション）
   */
  @GetMapping("/user/{id}/{pageIndex}")
  public ResponseEntity<Map<String, Object>> getFavplacesByUserId(@PathVariable Integer id, @PathVariable Integer pageIndex) {
    return ResponseEntity.ok().body(favplaceService.getFavplacesByUserId(id, pageIndex));
  }

  /**
   * Favplace新規登録
   * 
   * @param param
   * @param bindingResult
   * @return 新規登録したFavplace
   */
  @PostMapping("/create")
  public ResponseEntity<Map<String, Object>> saveFavplace(
    @RequestPart(name = "image", required = false) Optional<MultipartFile> image,
    @RequestPart("favplaceParams") @Validated(FavplaceParam.CreateGroup.class) FavplaceParam favplaceParams,
    @RequestPart(name = "scheduleParams", required = false) @Validated(ScheduleParam.CreateGroup.class) Optional<ScheduleParam> scheduleParams,
    BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      return ResponseEntity.badRequest().body(ResponseUtils.validationErrorResponse(bindingResult));
    }
    URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/favplaces/create").toUriString());
    return ResponseEntity.created(uri).body(favplaceService.saveFavplace(image, favplaceParams, scheduleParams));
  }

  /**
   * Favplace更新
   * 
   * @param param
   * @param bindingResult
   * @return 更新したFavplace
   */
  @PostMapping("/update")
  public ResponseEntity<Map<String, Object>> updateFavplace(
    @RequestPart(name = "image", required = false) Optional<MultipartFile> image,
    @RequestPart("favplaceParams") @Validated(FavplaceParam.UpdateDeleteGroup.class) FavplaceParam favplaceParams,
    @RequestPart(name = "scheduleParams", required = false) @Validated(ScheduleParam.UpdateDeleteGroup.class) Optional<ScheduleParam> scheduleParams,
    BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      return ResponseEntity.badRequest().body(ResponseUtils.validationErrorResponse(bindingResult));
    }
    return ResponseEntity.ok().body(favplaceService.updateFavplace(image, favplaceParams, scheduleParams));
  }
}