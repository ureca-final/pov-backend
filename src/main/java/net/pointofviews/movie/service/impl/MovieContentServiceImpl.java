package net.pointofviews.movie.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.pointofviews.common.service.S3Service;
import net.pointofviews.common.utils.ValidationUtils;
import net.pointofviews.movie.domain.Movie;
import net.pointofviews.movie.domain.MovieContent;
import net.pointofviews.movie.domain.MovieContentType;
import net.pointofviews.movie.exception.MovieContentException;
import net.pointofviews.movie.exception.MovieException;
import net.pointofviews.movie.repository.MovieContentRepository;
import net.pointofviews.movie.repository.MovieRepository;
import net.pointofviews.movie.service.MovieContentService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static net.pointofviews.movie.exception.MovieException.movieNotFound;

@Service
@RequiredArgsConstructor
public class MovieContentServiceImpl implements MovieContentService{

    private final MovieRepository movieRepository;
    private final MovieContentRepository movieContentRepository;
    private final S3Service s3Service;

    @Override
    @Transactional
    public List<String> saveMovieContentImages(Long movieId, List<MultipartFile> files) {

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> movieNotFound(movieId));

        List<String> imageUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            s3Service.validateImageFile(file);

            String uniqueFileName = s3Service.createUniqueFileName(file.getOriginalFilename());

            String filePath = "movies/" + movieId + "/" + uniqueFileName;

            // S3에 이미지 업로드
            String imageUrl = s3Service.saveImage(file, filePath);
            imageUrls.add(imageUrl);

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

        List<MovieContent> contents = movieContentRepository.findAllByIdAndType(ids, MovieContentType.IMAGE);

        // ID 개수 비교
        if (contents.size() != ids.size()) {
            throw MovieContentException.invalidMovieContentIds(ids.size(), contents.size());
        }

        // S3에서 각 URL의 파일 삭제
        for (MovieContent content : contents) {
            s3Service.deleteImage(content.getContent());
        }

        // MovieContent 레코드 삭제
        List<Long> contentIds = contents.stream()
                .map(MovieContent::getId)
                .toList();
        movieContentRepository.deleteAllByIds(contentIds);
    }

    @Override
    @Transactional
    public List<String> saveMovieContentVideos(Long movieId, List<String> urls) {

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> movieNotFound(movieId));

        for(String url : urls) {
            if (!ValidationUtils.isValidYouTubeUrl(url)) {
                throw MovieContentException.invalidYouTubeURL();
            }

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
    @Transactional
    public void deleteMovieContentVideos(List<Long> ids) {

        List<MovieContent> contents = movieContentRepository.findAllByIdAndType(ids, MovieContentType.YOUTUBE);

        // ID 개수 비교
        if (contents.size() != ids.size()) {
            throw MovieContentException.invalidMovieContentIds(ids.size(), contents.size());
        }

        // MovieContent 레코드 삭제
        List<Long> contentIds = contents.stream()
                .map(MovieContent::getId)
                .toList();
        movieContentRepository.deleteAllByIds(contentIds);
    }
}
