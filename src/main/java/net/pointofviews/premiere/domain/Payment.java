package net.pointofviews.premiere.domain;

import jakarta.persistence.*;
import net.pointofviews.common.domain.BaseEntity;

@Entity
public class Payment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private PaymentDomain paymentDomain;

    @ManyToOne(fetch = FetchType.LAZY)
    private Entry entry;

    private String paymentKey;
    private Integer price;
    private Integer amount;
}
