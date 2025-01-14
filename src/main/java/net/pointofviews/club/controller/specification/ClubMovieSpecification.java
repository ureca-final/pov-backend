package net.pointofviews.club.controller.specification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.pointofviews.auth.dto.MemberDetailsDto;
import net.pointofviews.club.dto.response.*;
import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.member.domain.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@Tag(name = "Club", description = "클럽 영화 북마크 관련 API")
public interface ClubMovieSpecification {

    @Operation(summary = "클럽 영화 북마크 조회", description = "클럽에서 저장한 영화 리스트를 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공"
            )
    })
    ResponseEntity<BaseResponse<ReadClubMoviesListResponse>> readMyClubMovies(
            @PathVariable UUID clubId,
            @AuthenticationPrincipal MemberDetailsDto memberDetails,
            @PageableDefault Pageable pageable);

}
