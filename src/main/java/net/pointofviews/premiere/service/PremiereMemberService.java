package net.pointofviews.premiere.service;

import net.pointofviews.premiere.dto.response.ReadDetailPremiereResponse;
import net.pointofviews.premiere.dto.response.ReadPremiereListResponse;

public interface PremiereMemberService {

    ReadPremiereListResponse findAllPremiere();

    ReadDetailPremiereResponse findPremiereDetail(Long premiereId);
}
