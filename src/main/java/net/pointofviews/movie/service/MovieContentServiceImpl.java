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
            // S3에 이미지 업로드
            String filePath = "movies/" + movieId + "/" + file.getOriginalFilename();
            String imageUrl = s3Service.saveImage(file, filePath);
            imageUrls.add(imageUrl);

            // MovieContent 엔티티 생성 및 저장
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

        // 2. S3에서 각 URL의 파일 삭제
        for (MovieContent content : contents) {
            String imageUrl = content.getContent();
            s3Service.deleteImage(imageUrl);
        }

        // 3. MovieContent 레코드 삭제
        movieContentRepository.deleteAll(contents);
    }

}
