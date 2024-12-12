package net.pointofviews.movie.dto.response;

import net.pointofviews.movie.domain.Movie;
import net.pointofviews.movie.domain.MovieCast;
import net.pointofviews.movie.domain.MovieCrew;
import net.pointofviews.people.domain.People;

import java.util.List;

public record CreditProcessorResponse(
        Movie movie,
        List<MovieCrew> crews,
        List<People> crewPeoples,
        List<MovieCast> casts,
        List<People> castPeoples
) {
}
