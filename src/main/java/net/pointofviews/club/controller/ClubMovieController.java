package net.pointofviews.club.controller;

import lombok.RequiredArgsConstructor;
import net.pointofviews.club.controller.specification.ClubMovieSpecification;
import net.pointofviews.club.dto.response.*;
import net.pointofviews.club.service.ClubMovieService;
import net.pointofviews.common.dto.BaseResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@PreAuthorize("permitAll()")
@RequestMapping("/api/clubs")
public class ClubMovieController implements ClubMovieSpecification {

    private final ClubMovieService clubMovieService;

    @GetMapping("/{clubId}/bookmark")
    @Override
    public ResponseEntity<BaseResponse<ReadClubMoviesListResponse>> readMyClubMovies(@PathVariable UUID clubId, @PageableDefault Pageable pageable) {
        ReadClubMoviesListResponse response = clubMovieService.readClubMovies(clubId, pageable);

        if (response.clubMovies().isEmpty()) {
            return BaseResponse.noContent();
        }

        return BaseResponse.ok("클럽 별 영화 북마크가 성공적으로 조회되었습니다.", response);
    }
}
