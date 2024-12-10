package net.pointofviews.premiere.service;

import net.pointofviews.member.domain.Member;
import net.pointofviews.premiere.dto.request.PremiereRequest;
import net.pointofviews.premiere.dto.response.ReadDetailPremiereResponse;
import org.springframework.web.multipart.MultipartFile;

public interface PremiereAdminService {

    void savePremiere(Member loginMember, PremiereRequest request);

    void updatePremiere(Member loginMember, Long premiereId, PremiereRequest request, MultipartFile file);

    void deletePremiere(Member loginMember, Long premiereId);

    void findAllPremiere(Member loginMember);

    ReadDetailPremiereResponse findPremiereDetail(Member loginMember, Long premiereId);
}
