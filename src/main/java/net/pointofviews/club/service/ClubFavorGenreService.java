package net.pointofviews.club.service;

import java.util.List;
import java.util.UUID;

public interface ClubFavorGenreService {
    List<String> readGenreNamesByClubId(UUID clubId);
}
