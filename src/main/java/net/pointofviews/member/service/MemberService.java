package net.pointofviews.member.service;

import net.pointofviews.member.dto.request.*;
import net.pointofviews.member.dto.response.*;

public interface MemberService {
    CreateMemberResponse signup(CreateMemberRequest request);

    LoginMemberResponse login(LoginMemberRequest request);

    void deleteMember();

    PutMemberGenreListResponse updateGenre(PutMemberGenreListRequest request);

    PutMemberImageResponse updateImage(PutMemberImageRequest request);

    PutMemberNicknameResponse updateNickname(PutMemberNicknameRequest request);

    PutMemberNoticeResponse updateNotice(PutMemberNoticeRequest request);
}
