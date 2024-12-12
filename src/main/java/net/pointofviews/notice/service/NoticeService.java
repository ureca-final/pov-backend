package net.pointofviews.notice.service;

import net.pointofviews.notice.dto.request.CreateNoticeTemplateRequest;
import net.pointofviews.notice.dto.request.SendNoticeRequest;
import net.pointofviews.notice.dto.response.CreateNoticeTemplateResponse;
import net.pointofviews.notice.dto.response.ReadNoticeResponse;

import java.util.List;
import java.util.UUID;

public interface NoticeService {
    CreateNoticeTemplateResponse saveNoticeTemplate(UUID adminId, CreateNoticeTemplateRequest request);

    void sendNotice(SendNoticeRequest request);

    List<ReadNoticeResponse> findNotices(UUID memberId);

    void updateNotice(UUID memberId, Long noticeId);
}