package net.pointofviews.member.service;

import net.pointofviews.auth.dto.request.CreateMemberRequest;
import net.pointofviews.auth.dto.request.LoginMemberRequest;
import net.pointofviews.auth.dto.response.CreateMemberResponse;
import net.pointofviews.auth.dto.response.LoginMemberResponse;
import net.pointofviews.member.domain.Member;
import net.pointofviews.member.dto.request.*;
import net.pointofviews.member.dto.response.*;

public interface MemberService {
    CreateMemberResponse signup(CreateMemberRequest request);

    LoginMemberResponse login(LoginMemberRequest request);

    void deleteMember(Member loginMember);

    PutMemberGenreListResponse updateGenre(PutMemberGenreListRequest request);

    PutMemberImageResponse updateImage(PutMemberImageRequest request);

    PutMemberNicknameResponse updateNickname(Member member, PutMemberNicknameRequest request);

    PutMemberNoticeResponse updateNotice(PutMemberNoticeRequest request);
}
