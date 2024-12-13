package net.pointofviews.payment.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.pointofviews.common.domain.BaseEntity;
import net.pointofviews.member.domain.Member;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Payment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    private String paymentKey;

    private String orderId;

    private String vendor;

    private Integer amount;

    private LocalDateTime requestedAt;

    private LocalDateTime approvedAt;


    @Builder
    public Payment(
            Member member,
            String paymentKey,
            String orderId,
            String vendor,
            Integer amount,
            LocalDateTime requestedAt,
            LocalDateTime approvedAt
    ) {
        Assert.notNull(paymentKey, "Payment key must not be null");

        this.member = member;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.vendor = vendor;
        this.amount = amount;
        this.requestedAt = requestedAt;
        this.approvedAt = approvedAt;
    }
}
