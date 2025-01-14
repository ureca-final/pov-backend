package net.pointofviews.payment.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.pointofviews.common.domain.BaseEntity;
import net.pointofviews.member.domain.Member;
import org.springframework.util.Assert;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TempPayment extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Member member;

    private String orderId;

    @Enumerated(EnumType.STRING)
    private OrderType type;

    private Integer amount;

    @Builder
    public TempPayment( Member member, String orderId, OrderType type, Integer amount) {
        Assert.notNull(orderId, "Order ID must not be null");

        this.member = member;
        this.orderId = orderId;
        this.type = type;
        this.amount = amount;
    }
}
