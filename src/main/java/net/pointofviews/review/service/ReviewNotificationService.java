package net.pointofviews.review.service;

import net.pointofviews.review.domain.Review;

public interface ReviewNotificationService {
    void sendReviewNotifications(Review review);
}
