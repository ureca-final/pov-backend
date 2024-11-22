package net.pointofviews.payment.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.pointofviews.common.domain.BaseEntity;
import net.pointofviews.curation.domain.CurationCategory;
import net.pointofviews.member.domain.Member;
import org.springframework.util.Assert;

@Entity
@Getter
@NoArgsConstructor
public class PaymentTransaction extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Payment payment;

    private String transactionKey;

    @Enumerated(EnumType.STRING)
    private PaymentType type;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private Integer price;

    @Builder
    public PaymentTransaction(Payment payment, String transactionKey, PaymentType type, PaymentStatus status, Integer price) {
        Assert.notNull(transactionKey, "Transaction key must not be null");

        this.payment = payment;
        this.transactionKey = transactionKey;
        this.type = type;
        this.status = status;
        this.price = price;
    }
}