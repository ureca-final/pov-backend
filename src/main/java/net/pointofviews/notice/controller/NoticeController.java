package net.pointofviews.notice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.pointofviews.auth.dto.MemberDetailsDto;
import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.notice.dto.request.CreateNoticeTemplateRequest;
import net.pointofviews.notice.dto.request.SendNoticeRequest;
import net.pointofviews.notice.dto.response.CreateNoticeTemplateResponse;
import net.pointofviews.notice.dto.response.ReadNoticeResponse;
import net.pointofviews.notice.service.NoticeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController implements NoticeSpecification {

    private final NoticeService noticeService;

    @PostMapping("/templates")
    @Override
    public ResponseEntity<BaseResponse<CreateNoticeTemplateResponse>> createNoticeTemplate(
            @Valid @RequestBody CreateNoticeTemplateRequest request,
            @AuthenticationPrincipal MemberDetailsDto memberDetailsDto
    ) {
        CreateNoticeTemplateResponse response = noticeService.saveNoticeTemplate(memberDetailsDto.member().getId(), request);
        return BaseResponse.ok("알림 템플릿이 성공적으로 생성되었습니다.", response);
    }

    @PostMapping("/send")
    @Override
    public ResponseEntity<BaseResponse<Void>> sendNotice(@Valid @RequestBody SendNoticeRequest request) {
        noticeService.sendNotice(request);
        return BaseResponse.ok("알림이 성공적으로 발송되었습니다.");
    }

    @GetMapping
    @Override
    public ResponseEntity<BaseResponse<List<ReadNoticeResponse>>> readNotices(
            @AuthenticationPrincipal MemberDetailsDto memberDetailsDto
    ) {
        List<ReadNoticeResponse> responses = noticeService.findNotices(memberDetailsDto.member().getId());
        return BaseResponse.ok("알림 목록을 성공적으로 조회했습니다.", responses);
    }

    @PutMapping("/{noticeId}/read")
    @Override
    public ResponseEntity<BaseResponse<Void>> putNotice(
            @PathVariable Long noticeId,
            @AuthenticationPrincipal MemberDetailsDto memberDetailsDto
    ) {
        noticeService.updateNotice(memberDetailsDto.member().getId(), noticeId);
        return BaseResponse.ok("알림을 성공적으로 읽음 처리했습니다. 알림 내역에서 삭제됩니다.");
    }
}