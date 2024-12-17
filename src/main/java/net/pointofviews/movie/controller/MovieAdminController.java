package net.pointofviews.movie.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.member.domain.Member;
import net.pointofviews.movie.controller.specification.MovieAdminSpecification;
import net.pointofviews.movie.dto.request.CreateMovieRequest;
import net.pointofviews.movie.dto.request.PutMovieRequest;
import net.pointofviews.movie.dto.response.*;
import net.pointofviews.movie.service.*;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping("/api/movies")
public class MovieAdminController implements MovieAdminSpecification {

    private final MovieContentService movieContentService;
    private final MovieApiSearchService movieApiSearchService;
    private final MovieAdminService movieAdminService;
    private final MovieService movieService;
    private final MovieSearchService movieSearchService;

    @Override
    @PostMapping
    public ResponseEntity<?> createMovie(@Valid @RequestBody CreateMovieRequest request) {
        movieService.saveMovie(request);

        return BaseResponse.ok("OK");
    }

    @Override
    @PutMapping("/{movieId}")
    public ResponseEntity<?> updateMovie(@PathVariable Long movieId, @RequestBody PutMovieRequest request) {
        movieService.updateMovie(movieId, request);
        return BaseResponse.ok("OK");
    }

    @Override
    @DeleteMapping("/{movieId}")
    public ResponseEntity<?> deleteMovie(@PathVariable Long movieId) {
        movieService.deleteMovie(movieId);
        return BaseResponse.noContent();
    }

    @Override
    @PostMapping("/{movieId}/images")
    public ResponseEntity<BaseResponse<List<String>>> createImages(
            @PathVariable Long movieId,
            @RequestParam("files") List<MultipartFile> files) {

        List<String> imageUrls = movieContentService.saveMovieContentImages(movieId, files);
        return BaseResponse.ok("이미지가 성공적으로 업로드되었습니다.", imageUrls);
    }

    @Override
    @PostMapping("/{movieId}/videos")
    public ResponseEntity<BaseResponse<List<String>>> createVideos(@PathVariable Long movieId, @RequestBody List<String> urls) {

        List<String> videoUrls = movieContentService.saveMovieContentVideos(movieId, urls);
        return BaseResponse.ok("영상 URL 이 성공적으로 업로드 되었습니다.", videoUrls);
    }

    @Override
    @DeleteMapping("/{movieId}/images")
    public ResponseEntity<BaseResponse<Void>> deleteImages(
            @PathVariable Long movieId,
            @RequestBody List<Long> ids) {

        movieContentService.deleteMovieContentImages(ids);
        return BaseResponse.ok("선택한 이미지가 성공적으로 삭제되었습니다.");
    }

    @Override
    @DeleteMapping("/{movieId}/videos")
    public ResponseEntity<BaseResponse<Void>> deleteVideos(
            @PathVariable Long movieId,
            @RequestBody List<Long> ids) {

        movieContentService.deleteMovieContentVideos(ids);
        return BaseResponse.ok("선택한 영상이 성공적으로 삭제되었습니다.");
    }

    @Override
    @GetMapping("/tmdb-search")
    public ResponseEntity<BaseResponse<SearchMovieApiListResponse>> searchTMDbMovieList(@RequestParam String query, @RequestParam(defaultValue = "1") int page) {
        SearchMovieApiListResponse searchMovieApiListResponse = movieApiSearchService.searchMovie(query, page);

        return BaseResponse.ok("OK", searchMovieApiListResponse);
    }

    @Override
    @GetMapping("/tmdb-search/{tmdbId}")
    public ResponseEntity<BaseResponse<SearchFilteredMovieDetailResponse>> searchTMDbMovie(@PathVariable String tmdbId) {
        SearchFilteredMovieDetailResponse response = movieApiSearchService.searchDetailsMovie(tmdbId);

        return BaseResponse.ok("OK", response);
    }

    @Override
    @GetMapping("/tmdb-search/{tmdbId}/credits")
    public ResponseEntity<BaseResponse<SearchCreditApiResponse>> searchTMDbCreditsLimit10(@PathVariable String tmdbId) {
        return BaseResponse.ok("OK", movieApiSearchService.searchLimit5Credit(tmdbId));
    }

    @Override
    @GetMapping("/tmdb-search/{tmdbId}/releases")
    public ResponseEntity<BaseResponse<SearchReleaseApiResponse>> searchTMDbReleases(@PathVariable String tmdbId) {
        return BaseResponse.ok("OK", movieApiSearchService.searchReleaseDate(tmdbId));
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/likes")
    public ResponseEntity<BaseResponse<ReadDailyMovieLikeListResponse>> readDailyMovieLikeList(
            @AuthenticationPrincipal(expression = "member") Member loginMember
    ) {
        ReadDailyMovieLikeListResponse response = movieAdminService.findDailyMovieLikeList(loginMember);

        if (response.movies().isEmpty()) {
            return BaseResponse.noContent();
        }

        return BaseResponse.ok("하루 동안 좋아요를 가장 많이 받은 영화 목록을 성공적으로 조회했습니다.", response);
    }

    @Override
    @GetMapping("/admin-search")
    public ResponseEntity<BaseResponse<AdminSearchMovieListResponse>> adminSearchMovies(
            @RequestParam String query, Pageable pageable) {
        AdminSearchMovieListResponse response = movieSearchService.adminSearchMovies(query, pageable);
        return BaseResponse.ok("영화 검색 결과 조회 성공", response);
    }

    @Override
    @GetMapping("/tmdb-search/trending")
    public ResponseEntity<BaseResponse<SearchMovieTrendingApiResponse>> adminSearchTrendingMovies(
            @RequestParam int page
    ) {
        SearchMovieTrendingApiResponse day = movieApiSearchService.searchTrendingMovie("day", page);
        return BaseResponse.ok("OK", day);
    }

}
