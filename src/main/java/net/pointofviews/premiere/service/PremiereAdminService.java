package net.pointofviews.premiere.service;

import net.pointofviews.member.domain.Member;
import net.pointofviews.premiere.dto.request.PremiereRequest;
import net.pointofviews.premiere.dto.response.ReadDetailPremiereResponse;

public interface PremiereAdminService {

    Void savePremiere(Member loginMember, PremiereRequest request);

    Void updatePremiere(Member loginMember, Long premiereId, PremiereRequest request);

    Void deletePremiere(Member loginMember, Long premiereId);

    Void findAllPremiere(Member loginMember);

    ReadDetailPremiereResponse findPremiereById(Member loginMember, Long premiereId);
}
