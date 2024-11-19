package net.pointofviews.member.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import net.pointofviews.common.domain.SoftDeleteEntity;

import java.time.LocalDate;
import java.util.UUID;

@Entity
public class Member extends SoftDeleteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String email;

    private String profileImage;

    private LocalDate birth;

    private String nickname;

    private String socialType;

    private String role;
}
