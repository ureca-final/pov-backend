package net.pointofviews.payment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.member.domain.Member;
import net.pointofviews.payment.dto.TempPaymentDto;
import net.pointofviews.payment.dto.request.ConfirmPaymentRequest;
import org.springframework.http.ResponseEntity;

@Tag(name = "Payment", description = "결제 관련 API")
public interface PaymentSpecification {

    @Operation(
            summary = "결제 임시 저장",
            description = "결제할 데이터를 임시 저장하는 API."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "결제 임시 저장 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "결제 데이터가 성공적으로 임시 저장되었습니다."
                                    }
                                    """)
                    )
            ),
            @ApiResponse(responseCode = "403", description = "결제 임시 저장 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "결제자와 응모자가 동일하지 않습니다."
                                    }
                                    """)
                    )
            )
    })
    ResponseEntity<BaseResponse<TempPaymentDto>> createTempPayment(Member loginMember, TempPaymentDto dto);

    @Operation(
            summary = "결제 저장",
            description = "결제가 승인된 데이터를 저장하는 API."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "결제 저장 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "결제가 성공적으로 승인되었습니다."
                                    }
                                    """)
                    )
            ),
            @ApiResponse(responseCode = "403", description = "결제 저장 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "결제자와 응모자가 동일하지 않습니다."
                                    }
                                    """)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "결제 저장 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "실제 결제 금액과 임시 결제 금액이 일치하지 않습니다."
                                    }
                                    """)
                    )
            )
    })
    ResponseEntity<BaseResponse<Void>> createPayment(Member loginMember, ConfirmPaymentRequest dto);

}
