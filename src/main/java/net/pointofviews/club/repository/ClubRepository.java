package net.pointofviews.club.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.pointofviews.club.domain.Club;

import java.util.UUID;

public interface ClubRepository extends JpaRepository<Club, UUID> {
}
