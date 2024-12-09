package net.pointofviews.club.service;

import jakarta.validation.Valid;
import net.pointofviews.club.dto.request.CreateClubRequest;
import net.pointofviews.club.dto.request.PutClubLeaderRequest;
import net.pointofviews.club.dto.request.PutClubRequest;
import net.pointofviews.club.dto.response.*;
import net.pointofviews.member.domain.Member;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Pageable;


import java.util.List;
import java.util.UUID;

public interface ClubService {
    CreateClubResponse saveClub(@Valid CreateClubRequest request, Member member);

    PutClubResponse updateClub(UUID clubId, @Valid PutClubRequest request, Member member);

    PutClubLeaderResponse updateClubLeader(UUID clubId, @Valid PutClubLeaderRequest request, Member member);

    void deleteClub(UUID clubId, Member member);

    void leaveClub(UUID clubId, Member member);

    CreateClubImageListResponse saveClubImages(List<MultipartFile> files, Member member);

    CreateClubImageListResponse updateClubImages(UUID clubId, List<MultipartFile> files, Member member);

    ReadAllClubsListResponse readAllPublicClubs();

    SearchClubsListResponse searchClubs(String query, Pageable pageable);

    ReadAllClubsListResponse readAllMyClubs(Member loginMember);

    ReadClubDetailsResponse readClubDetails(UUID clubId, Member loginMember, Pageable pageable);

}
