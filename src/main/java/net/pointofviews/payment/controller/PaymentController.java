package net.pointofviews.payment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.member.domain.Member;
import net.pointofviews.payment.dto.TempPaymentDto;
import net.pointofviews.payment.dto.request.ConfirmPaymentRequest;
import net.pointofviews.payment.service.PaymentService;
import net.pointofviews.payment.service.TempPaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
@RequestMapping("/api/payments")
public class PaymentController implements PaymentSpecification {

    private final PaymentService paymentService;
    private final TempPaymentService tempPaymentService;

    @Override
    @PostMapping("/temp")
    public ResponseEntity<BaseResponse<TempPaymentDto>> createTempPayment(
            @AuthenticationPrincipal(expression = "member") Member loginMember,
            @RequestBody @Valid TempPaymentDto request
    ) {
        TempPaymentDto response = tempPaymentService.saveTempPayment(loginMember, request);

        return BaseResponse.ok("결제 데이터가 성공적으로 임시 저장되었습니다.", response);
    }

    @Override
    @PostMapping
    public ResponseEntity<BaseResponse<Void>> createPayment(
            @AuthenticationPrincipal(expression = "member") Member loginMember,
            @RequestBody @Valid ConfirmPaymentRequest request
    ) {
        paymentService.confirmPayment(loginMember, request);

        return BaseResponse.ok("결제가 성공적으로 승인되었습니다.");
    }

}
