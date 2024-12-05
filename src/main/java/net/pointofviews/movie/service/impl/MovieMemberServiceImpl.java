package net.pointofviews.movie.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.pointofviews.member.domain.Member;
import net.pointofviews.movie.repository.MovieRepository;
import net.pointofviews.movie.service.MovieMemberService;
import org.springframework.stereotype.Service;

import static net.pointofviews.movie.exception.MovieException.movieNotFound;

@Service
@RequiredArgsConstructor
@Transactional
public class MovieMemberServiceImpl implements MovieMemberService {

    private final MovieRepository movieRepository;

    @Override
    public void updateMovieLike(long movieId, Member loginMember) {
        // 영화 존재 확인
        if(!movieRepository.existsById(movieId)) {
            throw movieNotFound(movieId);
        }

        // redis 좋아요 저장
    }
}
