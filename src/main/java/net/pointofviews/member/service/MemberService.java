package net.pointofviews.member.service;

import net.pointofviews.auth.dto.response.CheckLoginResponse;
import org.springframework.web.multipart.MultipartFile;

import net.pointofviews.auth.dto.request.CreateMemberRequest;
import net.pointofviews.auth.dto.request.LoginMemberRequest;
import net.pointofviews.auth.dto.response.CreateMemberResponse;
import net.pointofviews.member.domain.Member;
import net.pointofviews.member.dto.request.*;
import net.pointofviews.member.dto.response.*;

public interface MemberService {
    CreateMemberResponse signup(CreateMemberRequest request);

    CheckLoginResponse login(LoginMemberRequest request);

    void deleteMember(Member loginMember);

    PutMemberGenreListResponse updateGenre(Member loginMember, PutMemberGenreListRequest request);

    PutMemberImageResponse updateProfileImage(Member loginMember, MultipartFile file);

    PutMemberNicknameResponse updateNickname(Member loginMember, PutMemberNicknameRequest request);

    PutMemberNoticeResponse updateNotice(Member loginMember, PutMemberNoticeRequest request);

    void registerFcmToken(Member member, String fcmToken);
}
