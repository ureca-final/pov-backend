package net.pointofviews.movie.controller;

import lombok.RequiredArgsConstructor;
import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.movie.dto.SearchMovieCriteria;
import net.pointofviews.movie.dto.request.CreateMovieRequest;
import net.pointofviews.movie.dto.response.*;
import net.pointofviews.movie.service.MovieContentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor
public class MovieController implements MovieSpecification {

    private final MovieContentService movieContentService;


    @Override
    @PostMapping
    public ResponseEntity<?> createMovie(@RequestBody CreateMovieRequest request) {
        return null;
    }

    @Override
    @GetMapping
    public ResponseEntity<BaseResponse<SearchMovieListResponse>> searchMovieList(SearchMovieCriteria criteria) {
        return null;
    }

    @Override
    @GetMapping("/{movieId}")
    public ResponseEntity<BaseResponse<ReadDetailMovieResponse>> readDetailsMovie(@PathVariable Long movieId) {
        return null;
    }

    @Override
    @DeleteMapping("/{movieId}")
    public ResponseEntity<?> deleteMovie(@PathVariable Long movieId) {
        return null;
    }

    @Override
    @GetMapping("/search")
    public ResponseEntity<BaseResponse<SearchTMDBMovieListResponse>> tmdbSearchMovieList(@RequestParam String title) {
        return null;
    }

    @Override
    @PostMapping("/{movieId}/images")
    public ResponseEntity<BaseResponse<List<String>>> createImage(
            @PathVariable Long movieId,
            @RequestParam("files") List<MultipartFile> files) {

        try {
            // 서비스에 요청 전달
            List<String> imageUrls = movieContentService.saveMovieContentImages(movieId, files);

            // 성공 응답
            return BaseResponse.ok("이미지가 성공적으로 업로드되었습니다.", imageUrls);
        } catch (Exception e) {
            // 실패 응답
            return BaseResponse.internalServerError("이미지 업로드 중 오류가 발생했습니다.", null);
        }
    }

    @Override
    @PostMapping("/{movieId}/videos")
    public ResponseEntity<BaseResponse<List<String>>> createVideo(@PathVariable Long movieId, @RequestParam("ids") List<String> ids) {
        try {
            // 서비스에 요청 전달
            List<String> videoUrls = movieContentService.saveMovieContentVideos(movieId, ids);

            // 성공 응답
            return BaseResponse.ok("영상 URL 이 성공적으로 업로드 되었습니다.", videoUrls);
        } catch (Exception e) {
            // 실패 응답
            return BaseResponse.internalServerError("영상 URL 업로드 중 오류가 발생했습니다.", null);
        }
    }

    @Override
    @DeleteMapping("/{movieId}/images")
    public ResponseEntity<BaseResponse<Void>> deleteImages(
            @PathVariable Long movieId,
            @RequestBody List<Long> ids) {
        try {
            // 서비스 계층 호출
            movieContentService.deleteMovieContentImages(ids);

            // 성공 응답 반환
            return BaseResponse.ok("선택한 이미지가 삭제되었습니다.");
        } catch (Exception e) {
            // 실패 응답 반환
            return BaseResponse.internalServerError("이미지 삭제 중 오류가 발생했습니다.", null);
        }
    }

    @Override
    @DeleteMapping("/{movieId}/videos")
    public ResponseEntity<BaseResponse<Void>> deleteVideo(
            @PathVariable Long movieId,
            @RequestBody List<Long> ids) {
        try {
            // 서비스 계층 호출
            movieContentService.deleteMovieContentVideos(ids);

            // 성공 응답 반환
            return BaseResponse.ok("선택한 영상이 삭제되었습니다.");
        } catch (Exception e) {
            // 실패 응답 반환
            return BaseResponse.internalServerError("영상 삭제 중 오류가 발생했습니다.", null);
        }
    }

    @Override
    @PutMapping("/{movieId}/likes")
    public ResponseEntity<?> createMovieLike(Long movieId) {
        return null;
    }

}