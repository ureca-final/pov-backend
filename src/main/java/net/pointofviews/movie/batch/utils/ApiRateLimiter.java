package net.pointofviews.movie.batch.utils;

import java.util.concurrent.atomic.AtomicLong;

public class ApiRateLimiter {
    private static final long CALL_INTERVAL_MILLIS = 20; // 20ms
    private final AtomicLong lastCallTime = new AtomicLong(0);

    public void limit() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - lastCallTime.get();

        if (elapsedTime < CALL_INTERVAL_MILLIS) {
            try {
                Thread.sleep(CALL_INTERVAL_MILLIS - elapsedTime); // 남은 시간만큼 대기
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Rate limiter interrupted", e);
            }
        }

        lastCallTime.set(System.currentTimeMillis());
    }
}
