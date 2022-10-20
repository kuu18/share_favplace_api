package com.example.sharefavplace.api;

import java.net.URI;
import java.util.Map;

import com.example.sharefavplace.param.FavplaceParam;
import com.example.sharefavplace.service.FavplaceServiceImpl;
import com.example.sharefavplace.utils.ResponseUtils;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
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
   * Favplace新規登録
   * 
   * @param param
   * @param bindingResult
   * @return 新規登録したFavplace
   */
  @PostMapping("/create")
  public ResponseEntity<Map<String, Object>> saveFavplace(@RequestPart("image") MultipartFile image, @RequestPart("params") @Validated FavplaceParam params,
  BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      return ResponseEntity.badRequest().body(ResponseUtils.validationErrorResponse(bindingResult));
    }
    URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/favplaces/create").toUriString());
    return ResponseEntity.created(uri).body(favplaceService.saveFavplace(image, params));
  }
}