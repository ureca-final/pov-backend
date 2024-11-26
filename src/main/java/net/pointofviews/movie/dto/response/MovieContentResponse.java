package net.pointofviews.movie.dto.response;

import net.pointofviews.movie.domain.MovieContentType;

public record MovieContentResponse(
        Long id,

        Long movieId,

        String content,

        MovieContentType contentType
) {}
