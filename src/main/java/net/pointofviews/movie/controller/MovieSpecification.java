package net.pointofviews.movie.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.movie.dto.SearchMovieCriteria;
import net.pointofviews.movie.dto.request.CreateMovieRequest;
import net.pointofviews.movie.dto.response.ReadDetailMovieResponse;
import net.pointofviews.movie.dto.response.SearchMovieListResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Tag(name = "Movie", description = "영화 관련 API")
public interface MovieSpecification {

    @Operation(
            summary = "영화 단건 등록",
            description = "관리자가 새로운 영화를 등록할 때 사용하는 API."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "등록 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "영화등록이 성공적으로 완료되었습니다."
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "등록 실패 - 중복된 영화",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "이미 등록된 영화입니다."
                                    }
                                    """)
                    )
            )
    })
    ResponseEntity<?> createMovie(CreateMovieRequest request);

    @Operation(
            summary = "영화 목록 검색",
            description = "검색 조건에 따라 영화 목록을 조회하는 API."
    )
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
    ResponseEntity<BaseResponse<SearchMovieListResponse>> searchMovieList(SearchMovieCriteria criteria);

    @Operation(
            summary = "영화 상세 조회",
            description = "영화 단건 상세 조회 API"
    )
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
                                      "message": "존재하지 않는 영화입니다."
                                    }
                                    """)
                    )
            )
    })
    ResponseEntity<BaseResponse<ReadDetailMovieResponse>> readDetailsMovie(Long movieId);

    @Operation(
            summary = "영화 삭제",
            description = "영화 식별자를 이용해 서버에 등록된 영화를 삭제하는 API. "
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "삭제 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "영화 삭제가 성공적으로 완료되었습니다."
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "삭제 실패 - 없는 영화",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "존재하지 않는 영화입니다."
                                    }
                                    """)
                    )
            )
    })
    ResponseEntity<?> deleteMovie(Long movieId);

    @Operation(
            summary = "영화 TMDB 검색",
            description = "영화를 서버에 등록 전 TMDB에서 검색하기 위한 API."
    )
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
    ResponseEntity<?> tmdbSearchMovieList(
            @Parameter(
                    description = "검색할 영화 제목",
                    example = "Inception"
            )
            String title);
}