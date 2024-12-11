package net.pointofviews.curation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import net.pointofviews.curation.repository.CurationRepository;
import net.pointofviews.curation.service.impl.CurationMemberServiceImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;



@ExtendWith(MockitoExtension.class)
class CurationMemberServiceTest {

    @InjectMocks
    private CurationMemberServiceImpl curationService;

    @Mock
    private CurationRepository curationRepository;

    @Mock
    private CurationMovieRedisService curationMovieRedisService;

}