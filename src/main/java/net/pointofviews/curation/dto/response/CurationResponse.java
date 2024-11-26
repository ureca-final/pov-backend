package net.pointofviews.curation.dto.response;

import net.pointofviews.curation.domain.CurationCategory;

public record CurationResponse(
        Long id,
        String theme,
        CurationCategory category,
        String title,
        String description
) {}