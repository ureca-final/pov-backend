package net.pointofviews.movie.controller;

import lombok.RequiredArgsConstructor;
import net.pointofviews.club.service.ClubMovieService;
import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.member.domain.Member;
import net.pointofviews.movie.controller.specification.MovieSpecification;
import net.pointofviews.movie.dto.response.ReadDetailMovieResponse;
import net.pointofviews.movie.dto.response.SearchMovieListResponse;
import net.pointofviews.movie.service.MovieMemberService;
import net.pointofviews.movie.service.MovieSearchService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController implements MovieSpecification {

    private final MovieMemberService movieMemberService;
    private final MovieSearchService movieSearchService;
    private final ClubMovieService clubMovieService;

    @Override
    @GetMapping("/search")
    public ResponseEntity<BaseResponse<SearchMovieListResponse>> searchMovieList(@RequestParam String query,
                                                                                 Pageable pageable) {
        SearchMovieListResponse response = movieSearchService.searchMovies(query, pageable);
        return BaseResponse.ok("영화가 성공적으로 검색되었습니다.", response);
    }

    @Override
    @GetMapping("/{movieId}")
    public ResponseEntity<BaseResponse<ReadDetailMovieResponse>> readDetailsMovie(@PathVariable Long movieId) {
        ReadDetailMovieResponse readDetailMovieResponse = movieSearchService.readDetailMovie(movieId);

        return BaseResponse.ok("OK", readDetailMovieResponse);
    }

    @Override
    @PostMapping("/{movieId}/bookmark/{clubId}")
    public ResponseEntity<BaseResponse<Void>> saveMovieToMyClub(@PathVariable Long movieId, @PathVariable UUID clubId) {
        clubMovieService.saveMovieToMyClub(movieId, clubId);
        return BaseResponse.ok("내 클럽에 영화 북마크를 성공했습니다.");
    }

    @Override
    @PostMapping("/{movieId}/like")
    public ResponseEntity<BaseResponse<Void>> putMovieLike(@PathVariable Long movieId, @AuthenticationPrincipal(expression = "member") Member loginMember) {
        movieMemberService.updateMovieLike(movieId, loginMember);
        return BaseResponse.ok("영화 좋아요 등록이 성공적으로 완료되었습니다.");
    }

    @Override
    @PostMapping("/{movieId}/dislike")
    public ResponseEntity<BaseResponse<Void>> putMovieDislike(@PathVariable Long movieId, @AuthenticationPrincipal(expression = "member") Member loginMember) {
        movieMemberService.updateMovieDisLike(movieId, loginMember);
        return BaseResponse.ok("영화 좋아요 취소가 성공적으로 완료되었습니다.");
    }


}