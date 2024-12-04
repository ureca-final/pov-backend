package net.pointofviews.member.service;

import org.springframework.web.multipart.MultipartFile;

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

    PutMemberGenreListResponse updateGenre(Member loginMember, PutMemberGenreListRequest request);

    PutMemberImageResponse updateProfileImage(Member loginMember, MultipartFile file);

    PutMemberNicknameResponse updateNickname(Member loginMember, PutMemberNicknameRequest request);

    PutMemberNoticeResponse updateNotice(PutMemberNoticeRequest request);
}
