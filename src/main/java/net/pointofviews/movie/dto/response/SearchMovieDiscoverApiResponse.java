package net.pointofviews.movie.dto.response;

import java.util.List;

public record SearchMovieDiscoverApiResponse(
        int page,
        List<MovieResult> results,
        int total_pages,
        int total_results
) {
    public record MovieResult(
            String backdrop_path,
            List<Integer> genre_ids,
            int id,
            String original_title,
            String overview,
            String poster_path,
            String release_date,
            String title
    ) {
    }
}
