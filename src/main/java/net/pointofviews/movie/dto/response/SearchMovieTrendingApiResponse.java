package net.pointofviews.movie.dto.response;

import java.util.List;

public record SearchMovieTrendingApiResponse(
        Integer page,
        List<TrendingApiResponse> results
) {
    public record TrendingApiResponse(
            boolean adult,
            String backdrop_path,
            int id,
            String title,
            String original_language,
            String original_title,
            String overview,
            String poster_path,
            String media_type,
            List<Integer> genre_ids,
            double popularity,
            String release_date,
            boolean video,
            double vote_average,
            int vote_count
    ) {
    }
}
