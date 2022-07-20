package com.example.sharefavplace.handler;

import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.example.sharefavplace.dto.ResponseErrorDto;

@RestControllerAdvice
public class ExceptionHandler extends ResponseEntityExceptionHandler {

  @Override
  protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
      HttpStatus status, WebRequest request) {
    ResponseErrorDto re = new ResponseErrorDto(status.value(), ex.getMessage());
    return super.handleExceptionInternal(ex, re, headers, status, request);
  }

  /**
   * 画像サイズのエラーハンドリング
   * 
   * @param ex
   * @param request
   * @return
   */
  @org.springframework.web.bind.annotation.ExceptionHandler(SizeLimitExceededException.class)
  protected ResponseEntity<Object> handleSizeLimit(SizeLimitExceededException ex, WebRequest request) {
    ex = new SizeLimitExceededException("画像サイズは2MB以内にしてください。", ex.getActualSize(), ex.getPermittedSize());
    return handleExceptionInternal(ex, null, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  /**
   * 画像ファイルが選択されていない時のエラーハンドリング
   * 
   */
  @Override
  protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    ResponseErrorDto re = new ResponseErrorDto(status.value(), "ファイルを選択してください。");
    return super.handleExceptionInternal(ex, re, headers, status, request);
  }

}
