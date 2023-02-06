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
public class S3FileService {
  private final AmazonS3 s3Client;
  private final String s3BucketName = System.getenv("AWSS3_BUCKET_NAME");
	
  /**
   * AWSs3へファイルをアップロードする処理
   * 
   * @param fileUploadParam
   * @param s3PathName
   * @return
   * @throws IOException
   * @throws AmazonServiceException
   */
  public URL fileUpload(MultipartFile multipartFile, LocalDateTime createAt, String s3Path) {
    Optional<String> contentType = Optional.ofNullable(multipartFile.getContentType());
    contentType.orElseThrow(() -> new ApiRequestException("画像ファイルを選択してください。"));
    if (
      !contentType.get().equals("image/jpeg") &&
      !contentType.get().equals("image/jpg") &&
      !contentType.get().equals("image/png")
    ) {
      throw new ApiRequestException("画像ファイルを選択してください。");
    }
    DateTimeFormatter fm = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
		String extension = FilenameUtils.getExtension(multipartFile.getOriginalFilename()).toLowerCase();
    String fileName = createAt.format(fm) +"." + extension;
    File uploadFile = new File(fileName);
    s3Path = s3BucketName + s3Path;
    try (FileOutputStream uploadFileStream = new FileOutputStream(uploadFile)){
      byte[] bytes = multipartFile.getBytes();
      uploadFileStream.write(bytes);
      s3Client.putObject(s3Path, fileName, uploadFile);
      uploadFile.delete();
      return s3Client.getUrl(s3Path, fileName);
    } catch (AmazonServiceException | IOException e) {
      // TODO
      throw new RuntimeException(e);
    }
	}

  /**
   * AWSS3のファイルを削除する処理
   * 
   * @param bucketName
   * @param imageUrl
   * @return
   * @throws AmazonServiceException
   */
  public void fileDelete(String objectKey) {
    try {
      // S3オブジェクト削除
      s3Client.deleteObject(s3BucketName, objectKey);
    } catch (AmazonServiceException e) {
      // TODO
      throw new RuntimeException(e);
    }
  }

  /**
   * S3URLからS3オブジェクトキーを取得する
   * @param url
   * @return objectKey
   */
  public String getS3ObjectKeyFromUrl(String url) {
    return url.substring(url.lastIndexOf("/", url.lastIndexOf("/") - 1) + 1);
  }
}