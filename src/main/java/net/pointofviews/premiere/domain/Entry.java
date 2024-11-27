package net.pointofviews.premiere.domain;

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
public class Entry extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer amount;

    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private Premiere premiere;

    @Builder
    private Entry(Integer amount, Integer quantity, Member member, Premiere premiere) {
        Assert.notNull(quantity, "Amount must not be null");

        this.amount = amount;
        this.quantity = quantity;
        this.member = member;
        this.premiere = premiere;
    }
}
