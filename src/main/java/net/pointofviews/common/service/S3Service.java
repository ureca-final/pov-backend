package net.pointofviews.common.service;

import org.springframework.web.multipart.MultipartFile;

public interface S3Service {

    String saveImage(MultipartFile image, String filePath);
    void deleteImage(String imageAddress);

}
