package net.pointofviews.movie.controller.specification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.pointofviews.auth.dto.MemberDetailsDto;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Tag(name = "Movie-Member", description = "회원 영화 관련 API")
public interface MovieMemberSpecification {

    @Operation(summary = "영화 좋아요 토글", description = "영화 ID를 기반으로 사용자의 좋아요 상태를 토글하는 API.")
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
    ResponseEntity<?> putMovieLike(@AuthenticationPrincipal MemberDetailsDto memberDetailsDto, Long movieId);
}
