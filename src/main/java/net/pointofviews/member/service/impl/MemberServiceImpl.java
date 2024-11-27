package net.pointofviews.member.service.impl;

import lombok.RequiredArgsConstructor;
import net.pointofviews.auth.dto.request.CreateMemberRequest;
import net.pointofviews.auth.dto.request.LoginMemberRequest;
import net.pointofviews.auth.dto.response.CreateMemberResponse;
import net.pointofviews.auth.dto.response.LoginMemberResponse;
import net.pointofviews.member.dto.request.*;
import net.pointofviews.member.dto.response.*;
import net.pointofviews.member.service.MemberService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    @Override
    public CreateMemberResponse signup(CreateMemberRequest request) {
        return null;
    }

    @Override
    public LoginMemberResponse login(LoginMemberRequest request) {
        return null;
    }

    @Override
    public void deleteMember() {

    }

    @Override
    public PutMemberGenreListResponse updateGenre(PutMemberGenreListRequest request) {
        return null;
    }

    @Override
    public PutMemberImageResponse updateImage(PutMemberImageRequest request) {
        return null;
    }

    @Override
    public PutMemberNicknameResponse updateNickname(PutMemberNicknameRequest request) {
        return null;
    }

    @Override
    public PutMemberNoticeResponse updateNotice(PutMemberNoticeRequest request) {
        return null;
    }
}
