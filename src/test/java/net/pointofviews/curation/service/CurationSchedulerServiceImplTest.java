//package net.pointofviews.curation.service;
//
//import net.pointofviews.curation.domain.Curation;
//import net.pointofviews.curation.repository.CurationRepository;
//import net.pointofviews.curation.service.CurationRedisService;
//import net.pointofviews.curation.service.impl.CurationSchedulerServiceImpl;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThatCode;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class CurationSchedulerServiceImplTest {
//
//    @InjectMocks
//    private CurationSchedulerServiceImpl curationSchedulerService;
//
//    @Mock
//    private CurationRepository curationRepository;
//
//    @Mock
//    private CurationRedisService curationRedisService;
//
//    @Nested
//    class ActivateDailyCurations {
//
//        LocalDateTime todayStart;
//        LocalDateTime todayEnd;
//
//        @BeforeEach
//        void setUp() {
//            LocalDateTime now = LocalDateTime.now();
//            todayStart = now.toLocalDate().atStartOfDay();
//            todayEnd = todayStart.plusDays(1);
//        }
//
//        @Nested
//        class Success {
//
//            @Test
//            void 오늘_활성화될_큐레이션_캐시에_저장_성공() {
//                // given
//                Curation curation1 = Curation.builder().startTime(todayStart.plusHours(1)).build();
//                Curation curation2 = Curation.builder().startTime(todayStart.plusHours(2)).build();
//
//                // ID 필드를 강제로 설정
//                ReflectionTestUtils.setField(curation1, "id", 1L);
//                ReflectionTestUtils.setField(curation2, "id", 2L);
//
//                List<Curation> todaysCurations = List.of(curation1, curation2);
//
//                given(curationRepository.findByStartTimeBetween(todayStart, todayEnd)).willReturn(todaysCurations);
//
//                // when
//                assertThatCode(() -> curationSchedulerService.activateDailyCurations())
//                        .doesNotThrowAnyException();
//
//                // then
//                verify(curationRepository).findByStartTimeBetween(todayStart, todayEnd);
//                verify(curationRedisService).saveTodayCurationId(1L);
//                verify(curationRedisService).saveTodayCurationId(2L);
//                verifyNoMoreInteractions(curationRedisService);
//            }
//        }
//
//        @Nested
//        class Failure {
//
//            @Test
//            void 오늘_활성화될_큐레이션_없음() {
//                // given
//                given(curationRepository.findByStartTimeBetween(todayStart, todayEnd))
//                        .willReturn(List.of());
//
//                // when
//                assertThatCode(() -> curationSchedulerService.activateDailyCurations())
//                        .doesNotThrowAnyException();
//
//                // then
//                verify(curationRepository).findByStartTimeBetween(todayStart, todayEnd);
//                verifyNoInteractions(curationRedisService);
//            }
//        }
//    }
//}