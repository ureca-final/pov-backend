package net.pointofviews.movie.controller;

import lombok.RequiredArgsConstructor;
import net.pointofviews.auth.dto.MemberDetailsDto;
import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.movie.controller.specification.MovieMemberSpecification;
import net.pointofviews.movie.service.MovieMemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieMemberController implements MovieMemberSpecification {

    private final MovieMemberService movieMemberService;

    @Override
    @PutMapping("/{movieId}/likes")
    public ResponseEntity<BaseResponse<Void>> putMovieLike(
            @AuthenticationPrincipal MemberDetailsDto memberDetailsDto, @PathVariable Long movieId) {

        movieMemberService.updateMovieLike(movieId, memberDetailsDto.member());
        return BaseResponse.ok("좋아요가 성공적으로 ");
    }
}
