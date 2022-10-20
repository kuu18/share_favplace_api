package com.example.sharefavplace.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.example.sharefavplace.exceptions.ApiRequestException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileService {
  private final AmazonS3 s3Client;
	
  /**
   * AWSs3へファイルをアップロードする処理
   * 
   * @param fileUploadParam
   * @param s3PathName
   * @return
   * @throws IOException
   */
  public URL fileUpload(MultipartFile multipartFile, LocalDateTime createAt, String s3PathName) {
    Optional<String> contentType = Optional.ofNullable(multipartFile.getContentType());
    contentType.orElseThrow(() -> new ApiRequestException("画像ファイルを選択してください。"));
    if (
      !contentType.get().equals("image/jpeg") &&
      !contentType.get().equals("image/jpg") &&
      !contentType.get().equals("image/png")
    ) {
      throw new ApiRequestException("画像ファイルを選択してください。");
    }
    DateTimeFormatter fm = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
		String extension = FilenameUtils.getExtension(multipartFile.getOriginalFilename()).toLowerCase();
    String fileName = createAt.format(fm) +"." + extension;
    File uploadFile = new File(fileName);
    try (FileOutputStream uploadFileStream = new FileOutputStream(uploadFile)){
      byte[] bytes = multipartFile.getBytes();
      uploadFileStream.write(bytes);
      s3Client.putObject(s3PathName, fileName, uploadFile);
      uploadFile.delete();
      return s3Client.getUrl(s3PathName, fileName);
    } catch (AmazonServiceException | IOException e) {
      throw new RuntimeException(e);
    }
	}
}
