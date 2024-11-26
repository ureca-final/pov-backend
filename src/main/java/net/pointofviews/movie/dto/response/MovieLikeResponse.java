package net.pointofviews.movie.dto.response;

public record MovieLikeResponse(
        Long movieId,
        boolean isLiked,
        Long likeCount
) {}