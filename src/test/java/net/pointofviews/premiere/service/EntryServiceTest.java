package net.pointofviews.premiere.service;

import net.pointofviews.fixture.PremiereFixture;
import net.pointofviews.member.domain.Member;
import net.pointofviews.member.exception.MemberException;
import net.pointofviews.member.repository.MemberRepository;
import net.pointofviews.premiere.domain.Premiere;
import net.pointofviews.premiere.dto.request.CreateEntryRequest;
import net.pointofviews.premiere.dto.response.CreateEntryResponse;
import net.pointofviews.premiere.exception.EntryException;
import net.pointofviews.premiere.exception.PremiereException;
import net.pointofviews.premiere.repository.EntryRepository;
import net.pointofviews.premiere.repository.PremiereRepository;
import net.pointofviews.premiere.service.impl.EntryServiceImpl;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class EntryServiceTest {

    @InjectMocks
    private EntryServiceImpl entryService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PremiereRepository premiereRepository;

    @Mock
    private EntryRepository entryRepository;

    @Nested
    class SaveEntry {

        @Nested
        class Success {

            @Test
            void 시사회_선착순_100명_분산락_동시성_100명_응모_성공() throws InterruptedException {
                // given -- 테스트의 상태 설정
                int numThreads = 100;
                ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
                CountDownLatch latch = new CountDownLatch(numThreads);

                Premiere premiere = PremiereFixture.createPremiere(100);
                given(premiereRepository.findById(any())).willReturn(Optional.of(premiere));

                AtomicInteger successCount = new AtomicInteger(0);

                // when -- 테스트하고자 하는 행동
                for (int i = 0; i < numThreads; i++) {
                    Member member = mock(Member.class);
                    given(memberRepository.findById(any())).willReturn(Optional.of(member));

                    CreateEntryRequest request = new CreateEntryRequest(1, premiere.getAmount());

                    executorService.submit(() -> {
                        try {
                            entryService.saveEntry(member, 1L, request);
                            successCount.incrementAndGet();
                        } finally {
                            latch.countDown();
                        }
                    });
                }

                latch.await();

                // then -- 예상되는 변화 및 결과
                assertThat(successCount.get()).isEqualTo(numThreads);
            }

            @Test
            void 시사회_현재_남은_수량_보다_적은_수로_응모할_경우_응모_성공() {
                // given -- 테스트의 상태 설정
                Member member = mock(Member.class);
                Premiere premiere = PremiereFixture.createPremiere(10);

                given(memberRepository.findById(any())).willReturn(Optional.of(member));
                given(premiereRepository.findById(any())).willReturn(Optional.of(premiere));
                given(entryRepository.existsEntryByMemberIdAndPremiereId(any(), anyLong())).willReturn(false);
                given(entryRepository.countEntriesByPremiereId(anyLong())).willReturn(5L);

                CreateEntryRequest request = new CreateEntryRequest(1, premiere.getAmount());

                // when -- 테스트하고자 하는 행동
                CreateEntryResponse result = entryService.saveEntry(member, 1L, request);

                // then -- 예상되는 변화 및 결과
                assertThat(result).isNotNull();
            }
        }

        @Nested
        class Failure {

            @Test
            void 존재하지_않는_사용자_MemberException_memberNotFound_예외발생() {
                // given -- 테스트의 상태 설정
                Member member = mock(Member.class);
                UUID memberId = UUID.randomUUID();

                given(member.getId()).willReturn(memberId);
                given(memberRepository.findById(any())).willReturn(Optional.empty());

                // when -- 테스트하고자 하는 행동
                MemberException exception = assertThrows(MemberException.class, () ->
                        entryService.saveEntry(
                                member,
                                1L,
                                mock(CreateEntryRequest.class)
                        )
                );

                // then -- 예상되는 변화 및 결과
                assertSoftly(softly -> {
                    softly.assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
                    softly.assertThat(exception.getMessage()).isEqualTo(String.format("회원(Id: %s)이 존재하지 않습니다.", memberId));
                });
            }

            @Test
            void 존재하지_않는_시사회_PremiereException_premiereNotFound_예외발생() {
                // given -- 테스트의 상태 설정
                Member member = mock(Member.class);

                given(memberRepository.findById(any())).willReturn(Optional.of(member));
                given(premiereRepository.findById(any())).willReturn(Optional.empty());

                // when -- 테스트하고자 하는 행동
                PremiereException exception = assertThrows(PremiereException.class, () ->
                        entryService.saveEntry(
                                member,
                                -1L,
                                mock(CreateEntryRequest.class)
                        )
                );

                // then -- 예상되는 변화 및 결과
                assertSoftly(softly -> {
                    softly.assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
                    softly.assertThat(exception.getMessage()).isEqualTo(String.format("시사회(Id: %d)가 존재하지 않습니다.", -1L));
                });
            }

            @Test
            void 이미_응모한_회원_EntryException_duplicateEntry_예외발생() {
                // given -- 테스트의 상태 설정
                Member member = mock(Member.class);
                Premiere premiere = PremiereFixture.createPremiere();

                given(memberRepository.findById(any())).willReturn(Optional.of(member));
                given(premiereRepository.findById(any())).willReturn(Optional.of(premiere));
                given(entryRepository.existsEntryByMemberIdAndPremiereId(any(), anyLong())).willReturn(true);

                // when -- 테스트하고자 하는 행동
                EntryException exception = assertThrows(EntryException.class, () ->
                        entryService.saveEntry(
                                member,
                                1L,
                                mock(CreateEntryRequest.class)
                        )
                );

                // then -- 예상되는 변화 및 결과
                assertSoftly(softly -> {
                    softly.assertThat(exception.getStatus()).isEqualTo(HttpStatus.CONFLICT);
                    softly.assertThat(exception.getMessage()).isEqualTo("이미 응모한 시사회입니다.");
                });
            }

            @Test
            void 요청한_수량이_시사회_수량_초과_EntryException_quantityExceeded_예외발생() {
                // given -- 테스트의 상태 설정
                Member member = mock(Member.class);
                Premiere premiere = PremiereFixture.createPremiere(10);

                given(memberRepository.findById(any())).willReturn(Optional.of(member));
                given(premiereRepository.findById(any())).willReturn(Optional.of(premiere));
                given(entryRepository.existsEntryByMemberIdAndPremiereId(any(), anyLong())).willReturn(false);

                CreateEntryRequest request = new CreateEntryRequest(1, premiere.getAmount());
                given(entryRepository.countEntriesByPremiereId(anyLong())).willReturn(10L);

                // when -- 테스트하고자 하는 행동
                EntryException exception = assertThrows(EntryException.class, () ->
                        entryService.saveEntry(
                                member,
                                1L,
                                request
                        )
                );

                // then -- 예상되는 변화 및 결과
                assertSoftly(softly -> {
                    softly.assertThat(exception.getStatus()).isEqualTo(HttpStatus.CONFLICT);
                    softly.assertThat(exception.getMessage()).isEqualTo("시사회 응모 최대 인원 수를 초과했습니다.");
                });
            }
        }
    }
}
