package net.pointofviews.movie.batch.credit;

import lombok.RequiredArgsConstructor;
import net.pointofviews.movie.domain.Movie;
import net.pointofviews.movie.domain.MovieCast;
import net.pointofviews.movie.domain.MovieCrew;
import net.pointofviews.movie.dto.response.CreditProcessorResponse;
import net.pointofviews.movie.dto.response.SearchCreditApiResponse;
import net.pointofviews.movie.service.impl.MovieTMDbSearchService;
import net.pointofviews.people.domain.People;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TMDbMovieCreditProcessor implements ItemProcessor<Movie, CreditProcessorResponse> {

    private final static String IMAGE_PATH = "https://image.tmdb.org/t/p/w154";
    private final MovieTMDbSearchService searchService;

    public CreditProcessorResponse process(Movie item) {
        SearchCreditApiResponse creditResponse = searchService.searchLimit10Credit(item.getTmdbId().toString());

        List<SearchCreditApiResponse.CastResponse> cast = creditResponse.cast();
        List<People> castPeoples = cast.stream()
                .map(p -> People.builder().imageUrl(IMAGE_PATH + p.profile_path()).name(p.name()).tmdbId(p.id()).build())
                .toList();
        List<MovieCast> casts = cast.stream()
                .map(p -> MovieCast.builder().roleName(p.character()).displayOrder(p.order()).build())
                .toList();

        List<SearchCreditApiResponse.CrewResponse> crew = creditResponse.crew();
        List<People> crewPeople = crew.stream()
                .map(p -> People.builder().imageUrl(IMAGE_PATH + p.profile_path()).name(p.name()).tmdbId(p.id()).build())
                .toList();
        List<MovieCrew> crews = crew.stream()
                .map(p -> new MovieCrew(p.job()))
                .toList();

        return new CreditProcessorResponse(
                item, crews, crewPeople, casts, castPeoples
        );
    }
}
