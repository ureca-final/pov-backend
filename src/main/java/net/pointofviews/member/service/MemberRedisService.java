package net.pointofviews.member.service;

import java.util.List;
import java.util.UUID;

public interface MemberRedisService {
    void saveGenresToRedis(UUID memberId, List<String> genreCodes);
    void updateGenresInRedis(UUID memberId, List<String> existingGenreCodes, List<String> newGenreCodes);
}
