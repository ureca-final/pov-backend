package net.pointofviews.movie.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.movie.dto.request.MovieContentRequest;
import net.pointofviews.movie.dto.response.MovieContentResponse;
import net.pointofviews.movie.dto.response.MovieLikeResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Movie Content", description = "영화 관련 API")
public interface MovieSpecification {

    /** 영화 컨텐츠 이미지/영상 URL 등록,삭제 API **/

    // POST - 이미지 URL 등록
    @Operation(summary = "영화 이미지 URL 등록", description = "지정된 영화 ID에 이미지를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "⭕ SUCCESS",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                        {
                            "message": "SUCCESS",
                            "data": {
                                "id": 101,
                                "movieId": 1,
                                "content": "https://<bucketName>.s3.ap-northeast-2.amazonaws.com/<image.jpg>",
                                "contentType": "IMAGE"
                            }
                        }
                        """))
            )
    })
    ResponseEntity<BaseResponse<MovieContentResponse>> createImage(
            @PathVariable Long movieId,
            @RequestBody MovieContentRequest movieContentRequest
    );

    // POST - 영상 URL 등록
    @Operation(summary = "영화 영상 URL 등록", description = "지정된 영화 ID에 영상을 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "⭕ SUCCESS",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                        {
                            "message": "SUCCESS",
                            "data": {
                                "id": 102,
                                "movieId": 1,
                                "content": "https://www.youtube.com",
                                "contentType": "YOUTUBE"
                            }
                        }
                        """))
            )
    })
    ResponseEntity<BaseResponse<MovieContentResponse>> createVideo(
            @PathVariable Long movieId,
            @RequestBody MovieContentRequest movieContentRequest
    );

    // DELETE - 이미지 URL 삭제
    @Operation(summary = "영화 이미지 URL 삭제", description = "지정된 영화 ID의 특정 이미지 URL을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "이미지 URL이 성공적으로 삭제되었습니다."),
            @ApiResponse(responseCode = "404", description = "영화 또는 이미지 ID를 찾을 수 없습니다.")
    })
    ResponseEntity<BaseResponse<Void>> deleteImage(
            @PathVariable Long movieId,
            @PathVariable Long id
    );

    // DELETE - 영상 URL 삭제
    @Operation(summary = "영화 영상 URL 삭제", description = "지정된 영화 ID의 특정 영상 URL을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "영상 URL이 성공적으로 삭제되었습니다."),
            @ApiResponse(responseCode = "404", description = "영화 또는 영상 ID를 찾을 수 없습니다.")
    })
    ResponseEntity<BaseResponse<Void>> deleteVideo(
            @PathVariable Long movieId,
            @PathVariable Long id
    );

    /** 영화 좋아요 관련 API **/
    @Operation(
            summary = "영화 좋아요 토글",
            description = "영화 ID를 기반으로 사용자의 좋아요 상태를 토글하고, 총 좋아요 수를 반환합니다. " +
                    "좋아요를 누르지 않은 상태에서는 좋아요를 추가하고, 이미 좋아요를 누른 상태에서는 좋아요를 제거합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "⭕ SUCCESS",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                        {
                            "message": "SUCCESS",
                            "data": {
                                "movieId": 1,
                                "isLiked": true,
                                "likeCount": 10
                            }
                        }
                        """))
            ),
            @ApiResponse(responseCode = "404", description = "영화 또는 사용자를 찾을 수 없습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.")
    })
    ResponseEntity<BaseResponse<MovieLikeResponse>> toggleMovieLike(
            @PathVariable Long movieId
    );


}