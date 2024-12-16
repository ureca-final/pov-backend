package net.pointofviews.movie.service;

import net.pointofviews.member.domain.Member;
import net.pointofviews.movie.dto.MovieLikeCountListDto;
import net.pointofviews.movie.dto.MovieLikeListDto;

public interface MovieLikeRedisService {
    void saveLikedToRedis(Long movieId, Member member);
    void saveDisLikedToRedis(Long movieId, Member member);

    boolean readIsLikedFromRedis(Long movieId, Member member);
    Long readLikedCountFromRedis(Long movieId);

    MovieLikeListDto readAllLikedDataFromRedis();
    MovieLikeCountListDto readAllLikedCountFromRedis();
}
