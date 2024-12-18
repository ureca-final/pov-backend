package net.pointofviews.movie.controller.specification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.pointofviews.auth.dto.MemberDetailsDto;
import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.member.domain.Member;
import net.pointofviews.movie.dto.response.MovieListResponse;
import net.pointofviews.movie.dto.response.ReadDetailMovieResponse;
import net.pointofviews.movie.dto.response.SearchMovieListResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Tag(name = "Movie", description = "공통 영화 관련 API")
public interface MovieSpecification {

    @Operation(summary = "영화 상세 조회", description = "영화 단건 상세 조회 API.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공"
            )
    })
    ResponseEntity<BaseResponse<MovieListResponse>> MovieList(
            @AuthenticationPrincipal MemberDetailsDto memberDetails,
            Pageable pageable
    );

    @Operation(summary = "영화 목록 검색", description = "검색 조건에 따라 영화 목록을 조회하는 API.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "검색 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "검색 실패",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "잘못된 요청입니다."
                                    }
                                    """)
                    )
            )
    })
    ResponseEntity<BaseResponse<SearchMovieListResponse>> searchMovieList(
            @AuthenticationPrincipal MemberDetailsDto memberDetails,
            @RequestParam String query,
            Pageable pageable
    );


    @Operation(summary = "영화 상세 조회", description = "영화 단건 상세 조회 API.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "조회 실패",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "영화(Id: 1)는 존재하지 않습니다."
                                    }
                                    """)
                    )
            )
    })
    ResponseEntity<BaseResponse<ReadDetailMovieResponse>> readDetailsMovie(Long movieId, MemberDetailsDto memberDetails);

    @Operation(summary = "내 클럽 영화 북마크", description = "내 클럽에 영화 북마크 가입합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "영화 북마크 성공",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "내 클럽에 해당 영화 북마크가 완료되었습니다."
                                    }""")
                    )
            )
    })
    ResponseEntity<BaseResponse<Void>> saveMovieToMyClub(
            @PathVariable Long movieId,
            @PathVariable UUID clubId
    );




    @Operation(summary = "영화 좋아요 요청 기능", description = "영화 ID를 기반으로 사용자의 좋아요 요청 API")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "좋아요 요청 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "좋아요 요청이 성공적으로 등록되었습니다."
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "좋아요 요청 실패",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "잘못된 요청입니다."
                                    }
                                    """)
                    )
            )
    })
    ResponseEntity<?> putMovieLike(
            @PathVariable Long movieId,
            @AuthenticationPrincipal(expression = "member") Member loginMember
    );



    @Operation(summary = "영화 좋아요 취소 기능", description = "영화 ID를 기반으로 사용자의 좋아요 상태 취소 API")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "좋아요 취소",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "좋아요 요청이 성공적으로 등록되었습니다."
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "좋아요 취소 실패",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "잘못된 요청입니다."
                                    }
                                    """)
                    )
            )
    })
    ResponseEntity<?> putMovieDislike(
            @PathVariable Long movieId,
            @AuthenticationPrincipal(expression = "member") Member loginMember
    );
}
