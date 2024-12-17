package net.pointofviews.review.service.impl;

import lombok.RequiredArgsConstructor;
import net.pointofviews.club.domain.MemberClub;
import net.pointofviews.club.repository.ClubRepository;
import net.pointofviews.club.repository.MemberClubRepository;
import net.pointofviews.member.domain.Member;
import net.pointofviews.member.repository.MemberRepository;
import net.pointofviews.review.dto.response.ReadMyClubInfoListResponse;
import net.pointofviews.review.dto.response.ReadMyClubReviewListResponse;
import net.pointofviews.review.dto.response.ReadReviewResponse;
import net.pointofviews.review.service.ReviewClubService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static net.pointofviews.club.exception.ClubException.clubNotFound;
import static net.pointofviews.member.exception.MemberException.memberNotFound;
import static net.pointofviews.review.dto.response.ReadMyClubInfoListResponse.ReadMyClubInfoResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewClubServiceImpl implements ReviewClubService {

    private final MemberClubRepository memberClubRepository;
    private final ClubRepository clubRepository;
    private final MemberRepository memberRepository;

    @Override
    public ReadMyClubInfoListResponse findMyClubList(Member loginMember) {

        Member member = memberRepository.findById(loginMember.getId())
                .orElseThrow(() -> memberNotFound(loginMember.getId()));

        List<MemberClub> memberClubs = memberClubRepository.findClubsByMemberId(member.getId());

        List<ReadMyClubInfoResponse> clubs = new ArrayList<>();

        memberClubs.forEach(memberClub -> {
                    clubs.add(new ReadMyClubInfoResponse(
                            memberClub.getClub().getId(),
                            memberClub.getClub().getName(),
                            memberClub.getClub().getClubImage()
                    ));
                }
        );

        return new ReadMyClubInfoListResponse(clubs);
    }

    @Override
    public ReadMyClubReviewListResponse findReviewByClub(UUID clubId, Member loginMember, Pageable pageable) {

        if (clubRepository.findById(clubId).isEmpty()) {
            throw clubNotFound(clubId);
        }

        Slice<ReadReviewResponse> reviews = memberClubRepository.findReviewsWithLikesByClubId(clubId, loginMember.getId(), pageable);

        return new ReadMyClubReviewListResponse(clubId, reviews);
    }
}
