package net.pointofviews.movie.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MovieContentService {
    List<String> saveMovieContentImages(Long movieId, List<MultipartFile> files);
    void deleteMovieContentImages(List<Long> ids);
}
