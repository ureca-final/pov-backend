package net.pointofviews.movie.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pointofviews.member.exception.MemberException;
import net.pointofviews.member.repository.MemberRepository;
import net.pointofviews.movie.domain.MovieLike;
import net.pointofviews.movie.domain.MovieLikeCount;
import net.pointofviews.movie.dto.MovieLikeCountListDto;
import net.pointofviews.movie.dto.MovieLikeListDto;
import net.pointofviews.movie.exception.MovieException;
import net.pointofviews.movie.repository.MovieLikeCountRepository;
import net.pointofviews.movie.repository.MovieLikeRepository;
import net.pointofviews.movie.repository.MovieRepository;
import net.pointofviews.movie.service.MovieLikeRedisService;
import net.pointofviews.movie.service.MovieLikeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MovieLikeServiceImpl implements MovieLikeService {

    private final MovieLikeRedisService movieLikeRedisService;
    private final MovieLikeRepository movieLikeRepository;
    private final MovieRepository movieRepository;
    private final MemberRepository memberRepository;

    private final MovieLikeCountRepository movieLikeCountRepository;

    @Override
    public void saveLikedDataToDB() {
        MovieLikeListDto likes = movieLikeRedisService.readAllLikedDataFromRedis();

        // Redis 데이터를 DB Entity로 변환
        List<MovieLike> movieLikes = likes.movieLikeList().stream()
                .map(dto -> MovieLike.builder()
                        .movie(movieRepository.findById(dto.movieId())
                                .orElseThrow(() -> MovieException.movieNotFound(dto.movieId())))
                        .member(memberRepository.findById(dto.memeberId())
                                .orElseThrow(() -> MemberException.memberNotFound(dto.memeberId())))
                        .isLiked(dto.isLiked())
                        .build())
                .toList();

        // 변환된 데이터를 DB에 저장
        movieLikeRepository.saveAll(movieLikes);
    }

    @Override
    public void saveLikedCountDataToDB() {

        MovieLikeCountListDto counts = movieLikeRedisService.readAllLikedCountFromRedis();

        List<MovieLikeCount> movieLikeCounts = counts.movieLikeCountList().stream()
                .map(dto -> MovieLikeCount.builder()
                        .movie(movieRepository.findById(dto.movieId())
                                .orElseThrow(() -> MovieException.movieNotFound(dto.movieId())))
                        .likeCount(dto.likeCount())
                        .build())
                .toList();

        movieLikeCountRepository.saveAll(movieLikeCounts);
    }
}
