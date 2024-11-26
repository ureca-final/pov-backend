package net.pointofviews.payment.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.pointofviews.common.domain.BaseEntity;
import net.pointofviews.member.domain.Member;
import org.springframework.util.Assert;

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
    private String vendor;
    private Integer price;


    @Builder
    public Payment(Member member, String paymentKey, String vendor, Integer price) {
        Assert.notNull(paymentKey, "Payment key must not be null");

        this.member = member;
        this.paymentKey = paymentKey;
        this.vendor = vendor;
        this.price = price;
    }
}

