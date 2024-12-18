package net.pointofviews.movie.controller;

import lombok.RequiredArgsConstructor;
import net.pointofviews.auth.dto.MemberDetailsDto;
import net.pointofviews.club.service.ClubMovieService;
import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.member.domain.Member;
import net.pointofviews.movie.controller.specification.MovieSpecification;
import net.pointofviews.movie.dto.response.MovieListResponse;
import net.pointofviews.movie.dto.response.ReadDetailMovieResponse;
import net.pointofviews.movie.dto.response.SearchMovieListResponse;
import net.pointofviews.movie.service.MovieMemberService;
import net.pointofviews.movie.service.MovieSearchService;
import net.pointofviews.movie.service.MovieService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
@RequestMapping("/api/movies")
public class MovieController implements MovieSpecification {

    private final MovieMemberService memberService;
    private final MovieSearchService movieSearchService;
    private final MovieService movieService;
    private final ClubMovieService clubMovieService;

    @PreAuthorize("permitAll()")
    @Override
    @GetMapping
    public ResponseEntity<BaseResponse<MovieListResponse>> MovieList(@AuthenticationPrincipal MemberDetailsDto memberDetails,
                                                                     Pageable pageable) {
        UUID memberId = memberDetails != null ? memberDetails.member().getId() : null;

        MovieListResponse response = movieService.readMovies(memberId, pageable);
        return BaseResponse.ok("영화가 성공적으로 조회되었습니다.", response);
    }

    @PreAuthorize("permitAll()")
    @Override
    @GetMapping("/search")
    public ResponseEntity<BaseResponse<SearchMovieListResponse>> searchMovieList(
            @AuthenticationPrincipal MemberDetailsDto memberDetails,
            @RequestParam String query,
            Pageable pageable) {

        UUID memberId = memberDetails != null ? memberDetails.member().getId() : null;

        SearchMovieListResponse response = movieSearchService.searchMovies(query, memberId, pageable);
        return BaseResponse.ok("영화가 성공적으로 검색되었습니다.", response);
    }

    @Override
    @PreAuthorize("permitAll()")
    @GetMapping("/{movieId}")
    public ResponseEntity<BaseResponse<ReadDetailMovieResponse>> readDetailsMovie(@PathVariable Long movieId,
                                                                                  @AuthenticationPrincipal MemberDetailsDto memberDetails) {
        UUID memberId = Optional.ofNullable(memberDetails)
                .map(MemberDetailsDto::member)
                .map(Member::getId)
                .orElse(null);
        ReadDetailMovieResponse readDetailMovieResponse = movieSearchService.readDetailMovie(movieId, memberId);

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
        memberService.updateMovieLike(movieId, loginMember);
        return BaseResponse.ok("좋아요 등록이 완료되었습니다.");
    }

    @Override
    @PostMapping("/{movieId}/dislike")
    public ResponseEntity<BaseResponse<Void>> putMovieDislike(@PathVariable Long movieId, @AuthenticationPrincipal(expression = "member") Member loginMember) {
        memberService.updateMovieDisLike(movieId, loginMember);
        return BaseResponse.ok("좋아요 취소가 완료되었습니다.");
    }

}