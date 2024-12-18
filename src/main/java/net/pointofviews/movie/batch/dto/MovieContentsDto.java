package net.pointofviews.movie.batch.dto;

import net.pointofviews.movie.domain.MovieContent;

import java.util.List;

public record MovieContentsDto(
        List<MovieContent> movieContents
) {
}