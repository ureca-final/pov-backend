package net.pointofviews.movie.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.movie.dto.request.CreateMovieContentRequest;
import net.pointofviews.movie.dto.SearchMovieCriteria;
import net.pointofviews.movie.dto.request.CreateMovieRequest;
import net.pointofviews.movie.dto.response.ReadDetailMovieResponse;
import net.pointofviews.movie.dto.response.SearchMovieListResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
    @ApiResponses({@ApiResponse(
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




    /** 영화 컨텐츠 이미지/영상 URL 등록,삭제 API **/

    // POST - 이미지 URL 등록
    @Operation(summary = "영화 이미지 URL 등록", description = "지정된 영화 ID에 이미지를 등록합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "이미지 URL 등록 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "이미지 URL 등록이 성공적으로 완료되었습니다."
                                    }
                                    """)
                    )
            )
    })
    ResponseEntity<?> createImage(
            @PathVariable Long movieId,
            @RequestParam("files") List<MultipartFile> files
    );

    // POST - 영상 URL 등록
    @Operation(summary = "영화 영상 URL 등록", description = "지정된 영화 ID에 영상을 등록합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "영상 URL 등록 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "영상 URL 등록이 성공적으로 완료되었습니다."
                                    }
                                    """)
                    )
            )
    })
    ResponseEntity<?> createVideo(
            @PathVariable Long movieId,
            @RequestBody CreateMovieContentRequest createMovieContentRequest
    );

    // DELETE - 이미지 URL 삭제
    @Operation(summary = "영화 이미지 URL 삭제", description = "지정된 영화 ID의 특정 이미지 URL을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "삭제 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "이미지 URL이 성공적으로 삭제되었습니다."
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "삭제 실패 - 없는 영화 컨텐츠",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "존재하지 않는 이미지 URL입니다."
                                    }
                                    """)
                    )
            )
    })
    ResponseEntity<?> deleteImages(
            @PathVariable Long movieId,
            @RequestBody List<Long> ids
    );

    // DELETE - 영상 URL 삭제
    @Operation(summary = "영화 영상 URL 삭제", description = "지정된 영화 ID의 특정 영상 URL을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "삭제 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "영상 URL이 성공적으로 삭제되었습니다."
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "삭제 실패 - 없는 영화 컨텐츠",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "존재하지 않는 영상 URL 입니다."
                                    }
                                    """)
                    )
            )
    })
    ResponseEntity<?> deleteVideo(
            @PathVariable Long movieId,
            @PathVariable Long id
    );


    /** 영화 좋아요 관련 API **/
    @Operation(
            summary = "영화 좋아요 토글",
            description = "영화 ID를 기반으로 사용자의 좋아요 상태를 토글하고, 총 좋아요 수를 반환합니다. " +
                    "좋아요를 누르지 않은 상태에서는 좋아요를 추가하고, 이미 좋아요를 누른 상태에서는 좋아요를 제거합니다."
    )
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
                    responseCode = "404",
                    description = "좋아요 요청 실패 - 없는 영화 또는 사용자",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "영화 또는 사용자를 찾을 수 없습니다."
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
    ResponseEntity<?> createMovieLike(
            @PathVariable Long movieId
    );

}