package com.example.sharefavplace.exceptions;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@RestControllerAdvice
public class ApiExceptionHandller extends ResponseEntityExceptionHandler {

  /**
   * 画像サイズのエラーハンドリング
   * 
   * @param e
   * @param request
   * @return
   */
  @ExceptionHandler(SizeLimitExceededException.class)
  protected ResponseEntity<Object> handleSizeLimit(SizeLimitExceededException e, WebRequest request) {
    HttpStatus badRequest = HttpStatus.BAD_REQUEST;
    ApiException apiException = new ApiException(
      "画像サイズは2MB以内にしてください。",
      badRequest,
      ZonedDateTime.now(ZoneId.of("Asia/Tokyo"))
    );
    return new ResponseEntity<>(apiException, badRequest);
  }

  /**
   * ファイルが選択されていない時のエラーハンドリング
   * 
   */
  @Override
  protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException e,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    ApiException apiException = new ApiException(
      "ファイルを選択してください。",
      status,
      ZonedDateTime.now(ZoneId.of("Asia/Tokyo"))
    );
    return new ResponseEntity<>(apiException, status);
  }
  

  /**
   * badリクエストに関するExcptionハンドラー
   * 
   * @param e
   * @return
   */
  @ExceptionHandler(value = {ApiRequestException.class})
  public ResponseEntity<Object> handleApiRequestException(ApiRequestException e) {
    HttpStatus badRequest = HttpStatus.BAD_REQUEST;
    ApiException apiException = new ApiException(
      e.getMessage(),
      badRequest,
      ZonedDateTime.now(ZoneId.of("Asia/Tokyo"))
    );
    return new ResponseEntity<>(apiException, badRequest);
  }

  /**
   * 認証に関するExceptionハンドラー
   * 
   * @param e
   * @return
   */
  @ExceptionHandler(value = {ApiAuthException.class})
  public ResponseEntity<Object> handleApiAuthException(ApiAuthException e) {
    HttpStatus unAuthorized = HttpStatus.UNAUTHORIZED;
    ApiException apiException = new ApiException(
      e.getMessage(),
      unAuthorized,
      ZonedDateTime.now(ZoneId.of("Asia/Tokyo"))
    );
    return new ResponseEntity<>(apiException, unAuthorized);
  }

  /**
   * NotFoundに関するExceptionハンドラー
   * 
   * @param e
   * @return
   */
  @ExceptionHandler(value = {ApiNotFoundException.class})
  public ResponseEntity<Object> handleApiNotFoundException(ApiNotFoundException e) {
    HttpStatus notFound = HttpStatus.NOT_FOUND;
    ApiException apiException = new ApiException(
      e.getMessage(),
      notFound,
      ZonedDateTime.now(ZoneId.of("Asia/Tokyo"))
    );
    return new ResponseEntity<>(apiException, notFound);
  }
}
