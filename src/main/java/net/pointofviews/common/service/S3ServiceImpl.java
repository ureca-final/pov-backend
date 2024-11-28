package net.pointofviews.common.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pointofviews.common.exception.S3Exception;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;

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
            throw S3Exception.failedToUpload(e.getMessage());
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
            throw S3Exception.failedToDelete(e.getMessage());
        }
    }
}
