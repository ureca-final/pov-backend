package net.pointofviews.review.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record DeleteReviewImageListRequest(
        @Schema(
                description = "삭제할 이미지 URL 목록",
                example = """
            [
                "https://s3-bucket.../image1.jpg",
                "https://s3-bucket.../image2.jpg"
            ]
            """
        )
        List<String> imageUrls
) {}