package net.pointofviews.premiere.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
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
}
