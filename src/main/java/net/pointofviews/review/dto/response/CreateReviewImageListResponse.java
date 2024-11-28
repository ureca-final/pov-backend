package net.pointofviews.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record CreateReviewImageListResponse(
    @Schema(description = "업로드된 이미지 URL 목록",
            example = """
            [
                "https://s3-bucket.../image1.jpg",
                "https://s3-bucket.../image2.jpg"
            ]
            """
    )
    List<String> imageUrls
){}
