package net.pointofviews.movie.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.pointofviews.movie.domain.Movie;
import net.pointofviews.movie.domain.MovieContent;
import net.pointofviews.movie.domain.MovieContentType;
import net.pointofviews.movie.repository.MovieContentRepository;
import net.pointofviews.movie.repository.MovieRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MovieContentServiceImpl implements MovieContentService{

    private final MovieRepository movieRepository;
    private final MovieContentRepository movieContentRepository;
    private final S3Service s3Service;

    @Override
    @Transactional
    public List<String> saveMovieContentImages(Long movieId, List<MultipartFile> files) {

        // 1. 영화 엔티티 조회
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 영화 ID입니다: " + movieId));

        // 2. 결과 URL 리스트
        List<String> imageUrls = new ArrayList<>();

        // 3. 각 파일 처리
        for (MultipartFile file : files) {
            // 1. 원본 파일명 추출
            String originalFilename = file.getOriginalFilename();

            // 2. 파일 확장자 추출
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

            // 3. 파일명에서 확장자 제거 (기존 이름만 사용)
            String baseName = originalFilename.substring(0, originalFilename.lastIndexOf("."));

            // 4. UUID 앞 5글자를 기존 이름 뒤에 추가한 고유 파일명 생성
            String uniquePrefix = UUID.randomUUID().toString().substring(0, 5);
            String uniqueFileName = baseName + "_" + uniquePrefix + extension;

            // 5. S3에 업로드할 경로 생성
            String filePath = "movies/" + movieId + "/" + uniqueFileName;

            // 6. S3에 이미지 업로드
            String imageUrl = s3Service.saveImage(file, filePath);
            imageUrls.add(imageUrl);

            // 7. MovieContent 엔티티 생성 및 저장
            MovieContent movieContent = MovieContent.builder()
                    .movie(movie)
                    .content(imageUrl)
                    .contentType(MovieContentType.IMAGE)
                    .build();
            movieContentRepository.save(movieContent);
        }

        return imageUrls;
    }


    @Override
    @Transactional
    public void deleteMovieContentImages(List<Long> ids) {
        // 1. ID 리스트로 MovieContent 엔티티 조회
        List<MovieContent> contents = movieContentRepository.findAllById(ids);

        // 2. 존재하지 않는 ID 찾기
        List<Long> foundIds = contents.stream()
                .map(MovieContent::getId)
                .toList();
        List<Long> notFoundIds = ids.stream()
                .filter(id -> !foundIds.contains(id))
                .toList();
        if (!notFoundIds.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 이미지 ID가 포함되어 있습니다: " + notFoundIds);
        }

        // 2. IMAGE 타입 검증
        for (MovieContent content : contents) {
            if (content.getContentType() != MovieContentType.IMAGE) {
                throw new IllegalArgumentException("ID " + content.getId() + "은(는) IMAGE 타입이 아닙니다.");
            }
        }

        // 3. S3에서 각 URL의 파일 삭제
        for (MovieContent content : contents) {
            s3Service.deleteImage(content.getContent());
        }

        // 4. MovieContent 레코드 삭제
        movieContentRepository.deleteAll(contents);
    }

    @Override
    public List<String> saveMovieContentVideos(Long movieId, List<String> urls) {
        // 1. 영화 엔티티 조회
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 영화 ID입니다: " + movieId));

        // 2. URL 저장
        for(String url : urls) {
            // MovieContent 엔티티 생성 및 저장
            MovieContent movieContent = MovieContent.builder()
                    .movie(movie)
                    .content(url)
                    .contentType(MovieContentType.YOUTUBE)
                    .build();
            movieContentRepository.save(movieContent);
        }
        return urls;
    }

    @Override
    public void deleteMovieContentVideos(List<Long> ids) {
        // 1. ID 리스트로 MovieContent 엔티티 조회
        List<MovieContent> contents = movieContentRepository.findAllById(ids);

        // 존재하지 않는 ID 찾기
        List<Long> foundIds = contents.stream()
                .map(MovieContent::getId)
                .toList();
        List<Long> notFoundIds = ids.stream()
                .filter(id -> !foundIds.contains(id))
                .toList();
        if (!notFoundIds.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 이미지 ID가 포함되어 있습니다: " + notFoundIds);
        }

        // 2. YOUTUBE 타입 검증
        for (MovieContent content : contents) {
            if (content.getContentType() != MovieContentType.YOUTUBE) {
                throw new IllegalArgumentException("ID " + content.getId() + "은(는) YOUTUBE 타입이 아닙니다.");
            }
        }

        // 3. MovieContent 레코드 삭제
        movieContentRepository.deleteAll(contents);
    }
}
