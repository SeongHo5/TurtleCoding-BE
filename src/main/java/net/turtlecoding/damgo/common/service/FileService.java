package net.turtlecoding.damgo.common.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.turtlecoding.damgo.common.exception.ServiceFailedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;

import static net.turtlecoding.damgo.common.exception.enums.ExceptionStatus.FAILED_TO_UPLOAD_FILE;
import static net.turtlecoding.damgo.common.exception.enums.ExceptionStatus.UNSUPPORTED_FILE_FORMAT;


@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final AmazonS3Client objectStorageClient;

    private final String bucket = "turtlecoding-image-storage";
    private final static String[] SUPPORTED_IMAGE_FORMAT = {"jpg", "jpeg", "png", ""};

    /**
     * Object Storage 단일 파일 업로드
     * @param multipartFile 업로드할 파일(이미지만 가능)
     * @param dirName 업로드할 디렉토리 이름(없으면 생성됨)
     */
    public void uploadSingleFile(MultipartFile multipartFile, String dirName) {
        ObjectMetadata objectMetadata = new ObjectMetadata();

        checkSingleFileFormat(Objects.requireNonNull(multipartFile.getOriginalFilename()));

        multipartFile.getOriginalFilename();
        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.setContentType(multipartFile.getContentType());

        String fileName = dirName + "/" + multipartFile.getOriginalFilename();
        try {
            String uploadImageUrl = putFileToBucket(multipartFile.getInputStream(), fileName, objectMetadata);
            log.info("Uploaded Image to Object Storage : " + uploadImageUrl);
        } catch (IOException e) {
            throw new ServiceFailedException(FAILED_TO_UPLOAD_FILE);
        }
    }

    public void deleteSingleFile(String url, String dirName) {
        deleteFileFromBucket(url, dirName);
    }

    // ============== PRIVATE METHODS ==============

    /**
     * 단일 파일 형식(확장자) 체크
     */
    private void checkSingleFileFormat(String fileName) {
        int index = fileName.lastIndexOf(".");
        String extension = fileName.substring(index + 1).toLowerCase();
        boolean isSupported = Arrays.asList(SUPPORTED_IMAGE_FORMAT).contains(extension);
        if (!isSupported) {
            throw new ServiceFailedException(UNSUPPORTED_FILE_FORMAT);
        }
    }

    /**
     * 다중 파일 형식(확장자) 체크
     */
    private void checkMultipleFileFormat(String[] fileNames) {
        for (String fileName : fileNames) {
            checkSingleFileFormat(fileName);
        }
    }

    /**
     * Object Storage 단일 파일 업로드
     */
    private String putFileToBucket(InputStream file, String fileName, ObjectMetadata objectMetadata) {
        objectStorageClient.putObject(
                new PutObjectRequest(bucket, fileName, file, objectMetadata).withCannedAcl(
                        CannedAccessControlList.PublicRead));
        return objectStorageClient.getUrl(bucket, fileName).toString();
    }

    /**
     * Object Storage 단일 파일 삭제
     */
    private void deleteFileFromBucket(String url, String dirName) {
        final String[] split = url.split("/");
        final String fileName = dirName + "/" + split[split.length - 1];
        DeleteObjectRequest request = new DeleteObjectRequest(bucket, fileName);
        log.info("Deleted Image from Object Storage : " + request);
        objectStorageClient.deleteObject(request);
    }
}
