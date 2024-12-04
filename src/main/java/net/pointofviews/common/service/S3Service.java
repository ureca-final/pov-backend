package net.pointofviews.common.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface S3Service {

	String saveImage(MultipartFile image, String filePath);

	void deleteImage(String imageAddress);

	String getImage(String filePath);

	void validateImageFile(MultipartFile file);

	String createUniqueFileName(String originalFilename);

	List<String> extractImageUrlsFromHtml(String html);

	boolean isImageFile(String filename);

}
