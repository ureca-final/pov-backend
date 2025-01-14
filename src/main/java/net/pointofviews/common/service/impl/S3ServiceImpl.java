package net.pointofviews.common.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pointofviews.common.service.S3Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static net.pointofviews.common.exception.S3Exception.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

	private final AmazonS3 amazonS3;

	@Value("${cloud.aws.s3.bucketName}")
	private String bucketName;

	@Override
	public String saveImage(MultipartFile image, String filePath) {
		try {
			// InputStream 및 메타데이터 생성
			byte[] bytes = image.getBytes();
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentLength(bytes.length);
			metadata.setContentType(image.getContentType());

			// S3 업로드 요청 (ACL 설정 제거)
			amazonS3.putObject(new PutObjectRequest(bucketName, filePath, new ByteArrayInputStream(bytes), metadata));

			// 업로드된 파일의 URL 반환
			return amazonS3.getUrl(bucketName, filePath).toString();
		} catch (Exception e) {
			throw failedToUpload(e.getMessage());
		}
	}

	@Override
	public void deleteImage(String imageAddress) {
		// S3 객체 키 추출
		String objectKey = imageAddress.replace("https://" + bucketName + ".s3.ap-northeast-2.amazonaws.com/", "");

		try {
			// S3 객체 삭제 요청
			amazonS3.deleteObject(bucketName, objectKey);
			log.info("S3에서 이미지 삭제 성공: {}", objectKey); // 삭제 성공 로그
		} catch (Exception e) {
			log.error("S3에서 이미지 삭제 중 오류 발생: {}", e.getMessage(), e); // 에러 로그
			throw failedToDelete(e.getMessage());
		}
	}

	@Override
	public void deleteFolder(String folderPath) {
		try {
			ObjectListing objectListing = amazonS3.listObjects(bucketName, folderPath);
			while (true) {
				DeleteObjectsRequest deleteRequest = new DeleteObjectsRequest(bucketName);
				List<DeleteObjectsRequest.KeyVersion> keys = objectListing.getObjectSummaries().stream()
						.map(s3 -> new DeleteObjectsRequest.KeyVersion(s3.getKey()))
						.collect(Collectors.toList());

				if (!keys.isEmpty()) {
					deleteRequest.setKeys(keys);
					amazonS3.deleteObjects(deleteRequest);
					log.info("Batch deleted {} objects", keys.size());
				}

				if (objectListing.isTruncated()) {
					objectListing = amazonS3.listNextBatchOfObjects(objectListing);
				} else {
					break;
				}
			}
			log.info("S3에서 이미지 폴더 삭제 성공: {}", folderPath);
		} catch (Exception e) {
			log.error("S3에서 이미지 폴더 삭제 중 오류 발생: {}", folderPath, e);
			throw failedToDelete(e.getMessage());
		}
	}

	@Override
	public void moveImage(String sourceKey, String destinationKey) {
		try {
			// 복사
			CopyObjectRequest copyRequest = new CopyObjectRequest(bucketName, sourceKey, bucketName, destinationKey);
			amazonS3.copyObject(copyRequest);

			// 원본 삭제
			amazonS3.deleteObject(bucketName, sourceKey);

			log.info("S3 이미지 이동 성공: {} -> {}", sourceKey, destinationKey);
		} catch (Exception e) {
			log.error("S3 이미지 이동 중 오류 발생: {}", e.getMessage(), e);
			throw failedToMove(e.getMessage());
		}
	}

	@Override
	public String getImage(String filePath) {
		return amazonS3.getUrl(bucketName, filePath).toString();
	}

	@Override
	public void validateImageFile(MultipartFile file) {
		if (file.isEmpty()) {
			throw emptyImage();
		}

		if (file.getSize() > 2 * 1024 * 1024) {
			throw invalidImageSize();
		}

		String contentType = file.getContentType();
		if (contentType == null || !(
			contentType.equals("image/jpeg") || contentType.equals("image/jpg") || contentType.equals("image/png"))) {
			throw invalidImageFormat();
		}

		// 이미지 확장자 검증 추가
		String filename = file.getOriginalFilename();
		if (filename != null && !isImageFile(filename)) {
			throw invalidImageFormat();
		}
	}

	@Override
	public String createUniqueFileName(String originalFilename) {
		String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
		String uniquePrefix = UUID.randomUUID().toString();
		return uniquePrefix + extension;
	}

	@Override
	public boolean isImageFile(String filename) {
		String extension = filename.toLowerCase();
		return extension.endsWith(".jpg") ||
			   extension.endsWith(".jpeg") ||
			   extension.endsWith(".png");
	}
}
