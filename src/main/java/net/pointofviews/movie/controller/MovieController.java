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
    public ResponseEntity<BaseResponse<List<String>>> createImages(
            @PathVariable Long movieId,
            @RequestParam("files") List<MultipartFile> files) {

        try {
            // 1. 파일명 유효성 검사
            for (MultipartFile file : files) {
                String originalFilename = file.getOriginalFilename();
                if (!originalFilename.matches("^[a-zA-Z0-9._\\-]+$")) {
                    // 유효하지 않은 파일명일 경우 오류 응답 반환
                    return BaseResponse.badRequest(
                            "파일 이름은 영어, 숫자, 하이픈, 언더스코어, 또는 점만 포함해야 합니다: " + originalFilename,
                            null
                    );
                }
            }

            // 2. 서비스에 요청 전달
            List<String> imageUrls = movieContentService.saveMovieContentImages(movieId, files);

            // 3. 성공 응답
            return BaseResponse.ok("이미지가 성공적으로 업로드되었습니다.", imageUrls);

        } catch (Exception e) {
            // 4. 실패 응답
            return BaseResponse.internalServerError("이미지 업로드 중 오류가 발생했습니다.", null);
        }
    }

    @Override
    @PostMapping("/{movieId}/videos")
    public ResponseEntity<BaseResponse<List<String>>> createVideos(@PathVariable Long movieId, @RequestParam("ids") List<String> urls) {
        try {
            // 1. URL 유효성 검사
            for (String url : urls) {
                if (!isValidYouTubeUrl(url)) {
                    return BaseResponse.badRequest("유효하지 않은 유튜브 URL입니다: " + url, null);
                }
            }

            // 2. 서비스에 요청 전달
            List<String> videoUrls = movieContentService.saveMovieContentVideos(movieId, urls);

            // 3. 성공 응답
            return BaseResponse.ok("영상 URL 이 성공적으로 업로드 되었습니다.", videoUrls);
        } catch (Exception e) {
            // 4. 실패 응답
            return BaseResponse.internalServerError("영상 URL 업로드 중 오류가 발생했습니다.", null);
        }
    }

    @Override
    @DeleteMapping("/{movieId}/images")
    public ResponseEntity<BaseResponse<Void>> deleteImages(
            @PathVariable Long movieId,
            @RequestBody List<Long> ids) {
        try {
            // 1. ID 유효성 검사
            if (ids == null || ids.isEmpty()) {
                return BaseResponse.badRequest("삭제할 이미지 ID가 제공되지 않았습니다.", null);
            }

            // 2. 서비스 계층 호출
            movieContentService.deleteMovieContentImages(ids);

            // 3. 성공 응답 반환
            return BaseResponse.ok("선택한 이미지가 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            // 타입 검증 실패 응답 반환
            return BaseResponse.badRequest(e.getMessage(), null);
        } catch (Exception e) {
            // 기타 실패 응답 반환
            return BaseResponse.internalServerError("이미지 삭제 중 오류가 발생했습니다.", null);
        }
    }

    @Override
    @DeleteMapping("/{movieId}/videos")
    public ResponseEntity<BaseResponse<Void>> deleteVideos(
            @PathVariable Long movieId,
            @RequestBody List<Long> ids) {
        try {
            // 1. ID 유효성 검사
            if (ids == null || ids.isEmpty()) {
                return BaseResponse.badRequest("삭제할 영상 ID가 제공되지 않았습니다.", null);
            }

            // 2. 서비스 계층 호출
            movieContentService.deleteMovieContentVideos(ids);

            // 3. 성공 응답 반환
            return BaseResponse.ok("선택한 영상이 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            // 타입 검증 실패 응답 반환
            return BaseResponse.badRequest(e.getMessage(), null);
        } catch (Exception e) {
            // 기타 실패 응답 반환
            return BaseResponse.internalServerError("영상 삭제 중 오류가 발생했습니다.", null);
        }
    }

    @Override
    @PutMapping("/{movieId}/likes")
    public ResponseEntity<?> createMovieLike(Long movieId) {
        return null;
    }


    // 유튜브 도메인 유효성 검사
    private boolean isValidYouTubeUrl(String url) {
        String youtubeRegex = "^(https?://)?(www\\.)?(youtube\\.com|youtu\\.be)/.+$";
        return url.matches(youtubeRegex);
    }
}