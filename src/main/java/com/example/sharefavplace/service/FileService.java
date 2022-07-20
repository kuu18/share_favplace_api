package com.example.sharefavplace.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;

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
    public URL fileUpload(MultipartFile multipartFile, LocalDateTime createAt, String s3PathName) throws Exception {
    if (
      !multipartFile.getContentType().equals("image/jpeg") &&
      !multipartFile.getContentType().equals("image/jpg") &&
      !multipartFile.getContentType().equals("image/png")
    ) {
        throw new Exception("画像ファイルを選択してください。");
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
