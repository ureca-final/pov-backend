package net.pointofviews.common.service.impl;

import static net.pointofviews.common.exception.S3Exception.*;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import net.pointofviews.common.service.S3Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
		String baseName = originalFilename.substring(0, originalFilename.lastIndexOf("."));
		String uniquePrefix = UUID.randomUUID().toString();
		return baseName + "_" + uniquePrefix + extension;
	}

	@Override
	public List<String> extractImageUrlsFromHtml(String html) {
		List<String> imageUrls = new ArrayList<>();
		try {
			Document doc = Jsoup.parse(html);
			Elements imgTags = doc.select("img[src]");

			for (Element img : imgTags) {
				String imageUrl = img.attr("src");
				if (imageUrl.contains("s3")) {
					imageUrls.add(imageUrl);
				}
			}
			return imageUrls;
		} catch (Exception e) {
			throw failedToParseHtml(e.getMessage());
		}
	}

	@Override
	public boolean isImageFile(String filename) {
		String extension = filename.toLowerCase();
		return extension.endsWith(".jpg") ||
			   extension.endsWith(".jpeg") ||
			   extension.endsWith(".png");
	}
}
