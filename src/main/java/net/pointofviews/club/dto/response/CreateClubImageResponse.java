package net.pointofviews.club.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateClubImageResponse(
        @Schema(description = "업로드된 이미지 URL",
                example = "https://s3-bucket.../image.jpg")
        String imageUrl
) {
}
