package net.pointofviews.club.service;

import lombok.RequiredArgsConstructor;
import net.pointofviews.club.dto.request.CreateClubRequest;
import net.pointofviews.club.dto.request.PutClubLeaderRequest;
import net.pointofviews.club.dto.request.PutClubRequest;
import net.pointofviews.club.dto.response.CreateClubImageListResponse;
import net.pointofviews.club.dto.response.CreateClubResponse;
import net.pointofviews.club.dto.response.PutClubLeaderResponse;
import net.pointofviews.club.dto.response.PutClubResponse;
import net.pointofviews.member.domain.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClubServiceImpl implements ClubService {
    @Override
    public CreateClubResponse saveClub(CreateClubRequest request, Member member) {
        return null;
    }

    @Override
    public PutClubResponse updateClub(UUID clubId, PutClubRequest request, Member member) {
        return null;
    }

    @Override
    public PutClubLeaderResponse updateClubLeader(UUID clubId, PutClubLeaderRequest request, Member member) {
        return null;
    }

    @Override
    public Void deleteClub(UUID clubId, Member member) {
        return null;
    }

    @Override
    public Void leaveClub(UUID clubId, Member member) {
        return null;
    }

    @Override
    public CreateClubImageListResponse saveClubImages(List<MultipartFile> files, Member member) {
        return null;
    }

    @Override
    public CreateClubImageListResponse updateClubImages(List<MultipartFile> files, Member member) {
        return null;
    }
}
