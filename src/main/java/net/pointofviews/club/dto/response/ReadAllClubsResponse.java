package net.pointofviews.club.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "클럽 전체 조회 응답 DTO")
public record ReadAllClubsResponse(
        @Schema(description = "클럽 ID", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
        UUID clubId,

        @Schema(description = "클럽 이름", example = "화양동 민음사 북클럽")
        String clubName,

        @Schema(description = "클럽 설명", example = "화양동에는 딱 한명만 사는 화양동 민음사 북클럽📚")
        String clubDescription,

        @Schema(description = "참가자 수", example = "3")
        int participant,

        @Schema(description = "최대 참가자 수", example = "100")
        int maxParticipant,

        @Schema(description = "북마크 개수", example = "5")
        int clubMovieCount,

        @Schema(description = "클럽 선호 장르 해시태그", example = "[액션, SF]")
        List<String> clubFavorGenres
) {}