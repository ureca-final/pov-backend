package net.pointofviews.movie.domain;

import lombok.Getter;

@Getter
public enum KoreanFilmRating {
    ALL("ALL"),
    TWELVE("12"),
    FIFTEEN("15"),
    EIGHTEEN("18"),
    RESTRICTED("R"),
    NONE("N/A");

    private final String tmdbCode;

    KoreanFilmRating(String tmdbCode) {
        this.tmdbCode = tmdbCode;
    }

    public static KoreanFilmRating of(String tmdbCode) {
        for (KoreanFilmRating rating : values()) {
            if (rating.tmdbCode.equals(tmdbCode)) {
                return rating;
            }
        }
        return NONE;
    }
}