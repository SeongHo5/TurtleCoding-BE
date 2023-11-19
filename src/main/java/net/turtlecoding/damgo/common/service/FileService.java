package net.turtlecoding.damgo.common.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.turtlecoding.damgo.common.exception.ServiceFailedException;
import net.turtlecoding.damgo.product.dto.ProductInfoResponseDto;
import net.turtlecoding.damgo.product.entity.Product;
import net.turtlecoding.damgo.product.repository.ProductRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;

import static net.turtlecoding.damgo.common.exception.enums.ExceptionStatus.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final AmazonS3Client objectStorageClient;
    private final ProductRepository productRepository;

    private static final String bucket = "turtlecoding-image-storage";
    private static final String dirName = "images-product";
    private static final String IMAGE_FORMAT = "jpg";

    /**
     * Object Storage 단일 파일 업로드
     *
     * @param multipartFile 업로드할 파일(이미지만 가능)
     * @param dirName       업로드할 디렉토리 이름(없으면 생성됨)
     */
    public void uploadSingleFile(MultipartFile multipartFile) {
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

    public void deleteSingleFile(String url) {
        deleteFileFromBucket(url);
    }

    /**
     * 상품 이미지 URL 반환
     *
     * @param productId
     * @return 파일 URL
     */
    public ResponseEntity<ProductInfoResponseDto> getFileUrl(String productId) {
        Product product = productRepository.findByProdId(productId)
                .orElseThrow(() -> new ServiceFailedException(NOT_FOUND_PRODUCT));
        String imageUrl = objectStorageClient.getResourceUrl(bucket, dirName + "/" + productId + "." + IMAGE_FORMAT);
        return ResponseEntity.ok()
                .body(ProductInfoResponseDto.of(
                                imageUrl,
                                product.getCategory(),
                                product.getName(),
                                product.getPrice()
                        )
                );

    }


    // ============== PRIVATE METHODS ==============

    /**
     * 단일 파일 형식(확장자) 체크
     */
    private void checkSingleFileFormat(String fileName) {
        int index = fileName.lastIndexOf(".");
        String extension = fileName.substring(index + 1).toLowerCase();
        boolean isSupported = Arrays.asList(IMAGE_FORMAT).contains(extension);
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
    private void deleteFileFromBucket(String url) {
        final String[] split = url.split("/");
        final String fileName = dirName + "/" + split[split.length - 1];
        DeleteObjectRequest request = new DeleteObjectRequest(bucket, fileName);
        log.info("Deleted Image from Object Storage : " + request);
        objectStorageClient.deleteObject(request);
    }
}
