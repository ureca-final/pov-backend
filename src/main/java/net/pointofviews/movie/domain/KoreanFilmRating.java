package net.pointofviews.movie.domain;

import lombok.Getter;

@Getter
public enum KoreanFilmRating {
    ALL("ALL"),
    TWELVE("12"),
    FIFTEEN("15"),
    EIGHTEEN("18"),
    RESTRICTED("R");

    private final String tmdbCode;

    KoreanFilmRating(String tmdbCode) {
        this.tmdbCode = tmdbCode;
    }
}