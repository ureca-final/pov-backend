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
    public void savePremiere(Member loginMember, PremiereRequest premiere) {
    }

    @Override
    @Transactional
    public void updatePremiere(Member loginMember, Long premiereId, PremiereRequest request) {

        if (memberRepository.findById(loginMember.getId()).isEmpty()) {
            throw adminNotFound(loginMember.getId());
        }

        Premiere premiere = premiereRepository.findById(premiereId)
                .orElseThrow(() -> premiereNotFound(premiereId));

        premiere.updatePremiere(request);
    }

    @Override
    public void deletePremiere(Member loginMember, Long premiereId) {
    }

    @Override
    public void findAllPremiere(Member loginMember) {
    }

    @Override
    public ReadDetailPremiereResponse findPremiereById(Member loginMember, Long premiereId) {
        return null;
    }
}
