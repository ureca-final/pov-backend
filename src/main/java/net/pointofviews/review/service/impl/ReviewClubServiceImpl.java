package net.pointofviews.review.service.impl;

import static net.pointofviews.member.exception.MemberException.*;
import static net.pointofviews.review.dto.response.ReadMyClubInfoListResponse.*;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.pointofviews.club.domain.MemberClub;
import net.pointofviews.club.repository.MemberClubRepository;
import net.pointofviews.member.domain.Member;
import net.pointofviews.member.repository.MemberRepository;
import net.pointofviews.review.dto.response.ReadMyClubInfoListResponse;
import net.pointofviews.review.dto.response.ReadMyClubReviewsResponse;
import net.pointofviews.review.service.ReviewClubService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewClubServiceImpl implements ReviewClubService {

	private final MemberClubRepository memberClubRepository;
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
	public ReadMyClubReviewsResponse findReviewByClub() {
		return null;
	}
}
