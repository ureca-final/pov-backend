package net.pointofviews.movie.service;

import net.pointofviews.member.domain.Member;

public interface MovieMemberService {
    void updateMovieLike(long movieId, Member loginMember);
    void updateMovieDisLike(Long movieId, Member loginMember);
}
