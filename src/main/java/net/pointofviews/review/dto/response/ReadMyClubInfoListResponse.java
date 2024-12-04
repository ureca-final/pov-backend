package net.pointofviews.review.dto.response;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response containing a list of clubs")
public record ReadMyClubInfoListResponse(

	@Schema(description = "가입한 클럽 목록")
	List<ReadMyClubInfoResponse> clubs

) {
	@Schema(description = "가입한 클럽 조회 응답 DTO")
	public record ReadMyClubInfoResponse(

		@Schema(description = "클럽 ID", example = "a1s2d3f4@q7w8e9")
		UUID clubId,

		@Schema(description = "클럽 이름", example = "영사모")
		String clubName,

		@Schema(description = "클럽 대표 이미지", example = "https://example.com/clubs/club.jpg")
		String clubImage

	) {
	}
}
