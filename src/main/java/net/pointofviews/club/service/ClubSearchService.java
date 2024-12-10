package net.pointofviews.club.service;

import net.pointofviews.club.dto.response.SearchClubsListResponse;
import org.springframework.data.domain.Pageable;

public interface ClubSearchService {
    SearchClubsListResponse searchClubs(String query, Pageable pageable);
}
