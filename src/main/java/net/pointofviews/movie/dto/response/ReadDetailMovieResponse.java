package net.pointofviews.movie.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import net.pointofviews.movie.domain.MovieCast;
import net.pointofviews.movie.domain.MovieCrew;
import net.pointofviews.review.dto.ReviewDetailsWithLikeCountDto;
import net.pointofviews.review.dto.ReviewPreferenceCountDto;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "영화 상세 조회 응답 DTO")
public record ReadDetailMovieResponse(
        @Schema(description = "영화 제목", example = "인셉션")
        String title,

        @Schema(description = "출시일", example = "2010-07-16", format = "date")
        LocalDate released,

        @Schema(description = "영화 장르 목록", example = "[\"액션\", \"드라마\"]")
        List<String> genre,

        @Schema(description = "영화 좋아요 수", example = "12345")
        Long movieLikeCount,

        @Schema(description = "리뷰 선호도 요약 정보 목록", example = "[{\"goodCount\": 10, \"badCount\": 2}]")
        List<ReviewPreferenceCountDto> preferenceCounts,

        @Schema(description = "줄거리", example = "A skilled thief is given a chance to erase his criminal record by completing an impossible heist.")
        String plot,

        @Schema(description = "감독 정보 목록", example = "[{\"id\": 1, \"name\": \"크리스토퍼 놀란\", \"profileImage\": \"https://example.com/nolan.jpg\", \"role\": \"Director\"}]")
        List<ReadMovieCrewResponse> directors,

        @Schema(description = "출연 배우 목록", example = "[{\"id\": 1, \"name\": \"Leonardo DiCaprio\", \"profileImage\": \"https://example.com/dicaprio.jpg\", \"role\": \"Dom Cobb\", \"order\": 1}]")
        List<ReadMovieCastResponse> actors,

        @Schema(description = "포스터 URL", example = "https://example.com/poster.jpg")
        String poster,

        @Schema(description = "배경 URL", example = "backdrop.jpg")
        String backdrop,

        @Schema(description = "제작 국가 목록", example = "[\"USA\", \"UK\"]")
        List<String> country,

        @Schema(description = "이미지 목록", example = "[\"https://example.com/image1.jpg\", \"https://example.com/image2.jpg\"]")
        List<String> images,

        @Schema(description = "비디오 목록", example = "[\"https://example.com/video1.mp4\", \"https://example.com/video2.mp4\"]")
        List<String> videos,

        @Schema(description = "좋아요 여부", example = "false")
        Boolean isLiked,

        @Schema(description = "리뷰 상세 정보 목록")
        List<ReviewDetailsWithLikeCountDto> reviews
) {
    public record ReadMovieCastResponse(
            @Schema(description = "출연진 ID", example = "1")
            Long id,

            @Schema(description = "출연진 이름", example = "Leonardo DiCaprio")
            String name,

            @Schema(description = "출연진 프로필 이미지 URL", example = "https://example.com/dicaprio.jpg")
            String profileImage,

            @Schema(description = "출연진 역할", example = "Dom Cobb")
            String role,

            @Schema(description = "출연진 순서", example = "1")
            Integer order
    ) {
        public static ReadMovieCastResponse of(MovieCast cast) {
            return new ReadMovieCastResponse(
                    cast.getId(),
                    cast.getPeople().getName(),
                    cast.getPeople().getImageUrl(),
                    cast.getRoleName(),
                    cast.getDisplayOrder());
        }
    }

    public record ReadMovieCrewResponse(
            @Schema(description = "스태프 ID", example = "1")
            Long id,

            @Schema(description = "스태프 이름", example = "크리스토퍼 놀란")
            String name,

            @Schema(description = "스태프 프로필 이미지 URL", example = "https://example.com/nolan.jpg")
            String profileImage,

            @Schema(description = "스태프 역할", example = "Director")
            String role
    ) {
        public static ReadMovieCrewResponse of(MovieCrew crew) {
            return new ReadMovieCrewResponse(
                    crew.getId(),
                    crew.getPeople().getName(),
                    crew.getPeople().getImageUrl(),
                    crew.getRole()
            );
        }
    }
}