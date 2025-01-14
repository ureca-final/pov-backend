package net.pointofviews.premiere.service;

import net.pointofviews.member.domain.Member;
import net.pointofviews.premiere.dto.request.PremiereRequest;
import net.pointofviews.premiere.dto.response.ReadDetailPremiereResponse;
import net.pointofviews.premiere.dto.response.ReadPremierePageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface PremiereAdminService {

    void savePremiere(Member loginMember, PremiereRequest request, MultipartFile eventImage, MultipartFile thumbnail);

    void updatePremiere(Member loginMember, Long premiereId, PremiereRequest request, MultipartFile eventImage, MultipartFile thumbnail);

    void deletePremiere(Member loginMember, Long premiereId);

    ReadPremierePageResponse findAllPremiere(Member loginMember, Pageable pageable);

    ReadDetailPremiereResponse findPremiereDetail(Member loginMember, Long premiereId);
}
