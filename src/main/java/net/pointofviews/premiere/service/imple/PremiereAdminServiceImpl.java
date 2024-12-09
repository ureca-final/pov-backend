package net.pointofviews.premiere.service.imple;

import lombok.RequiredArgsConstructor;
import net.pointofviews.member.domain.Member;
import net.pointofviews.member.repository.MemberRepository;
import net.pointofviews.premiere.domain.Premiere;
import net.pointofviews.premiere.dto.request.PremiereRequest;
import net.pointofviews.premiere.dto.response.ReadDetailPremiereResponse;
import net.pointofviews.premiere.repository.PremiereRepository;
import net.pointofviews.premiere.service.PremiereAdminService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static net.pointofviews.member.exception.MemberException.adminNotFound;
import static net.pointofviews.premiere.exception.PremiereException.premiereNotFound;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PremiereAdminServiceImpl implements PremiereAdminService {

    private final PremiereRepository premiereRepository;
    private final MemberRepository memberRepository;

    @Override
    public Void savePremiere(Member loginMember, PremiereRequest premiere) {
        return null;
    }

    @Override
    @Transactional
    public Void updatePremiere(Member loginMember, Long premiereId, PremiereRequest request) {

        if (memberRepository.findById(loginMember.getId()).isEmpty()) {
            throw adminNotFound(loginMember.getId());
        }

        Premiere premiere = premiereRepository.findById(premiereId)
                .orElseThrow(() -> premiereNotFound(premiereId));

        premiere.updatePremiere(request);

        return null;
    }

    @Override
    public Void deletePremiere(Member loginMember, Long premiereId) {
        return null;
    }

    @Override
    public Void findAllPremiere(Member loginMember) {
        return null;
    }

    @Override
    public ReadDetailPremiereResponse findPremiereById(Member loginMember, Long premiereId) {
        return null;
    }
}
