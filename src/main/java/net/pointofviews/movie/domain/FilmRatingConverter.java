package net.pointofviews.movie.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class FilmRatingConverter implements AttributeConverter<KoreanFilmRating, String> {

    @Override
    public String convertToDatabaseColumn(KoreanFilmRating attribute) {
        return attribute != null ? attribute.getTmdbCode() : null;
    }

    @Override
    public KoreanFilmRating convertToEntityAttribute(String dbData) {
        return KoreanFilmRating.of(dbData);
    }
}
