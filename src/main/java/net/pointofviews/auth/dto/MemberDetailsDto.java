package net.pointofviews.auth.dto;

import net.pointofviews.member.domain.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public record MemberDetailsDto(
        Member member
) implements UserDetails {

    public static MemberDetailsDto from(Member member) {
        return new MemberDetailsDto(member);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + member.getRoleType().name()));
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return member.getEmail();
    }
}