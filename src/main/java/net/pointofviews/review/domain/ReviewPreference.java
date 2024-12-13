package net.pointofviews.review.domain;

import net.pointofviews.review.exception.ReviewException;

public enum ReviewPreference {
    GOOD, BAD;

    public static ReviewPreference from(String preference) {
        String processedPreference = preference.toLowerCase();

        return switch (processedPreference) {
            case "good" -> GOOD;
            case "bad" -> BAD;
            default -> throw ReviewException.undefinedPreference(preference);
        };
    }
}
