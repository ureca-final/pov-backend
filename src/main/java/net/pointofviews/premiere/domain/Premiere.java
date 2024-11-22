package net.pointofviews.premiere.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Premiere {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String eventImage;

    private boolean isPaymentRequired;

    private LocalDateTime startAt;

    @Builder
    private Premiere(String title, String content, String eventImage, boolean isPaymentRequired, LocalDateTime startAt) {
        this.title = title;
        this.content = content;
        this.eventImage = eventImage;
        this.isPaymentRequired = isPaymentRequired;
        this.startAt = startAt;
    }
}
