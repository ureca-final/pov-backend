//package net.pointofviews.curation.service;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.BDDMockito.*;
//
//import net.pointofviews.curation.domain.Curation;
//import net.pointofviews.curation.domain.CurationCategory;
//import net.pointofviews.curation.dto.request.CreateCurationRequest;
//import net.pointofviews.curation.dto.response.*;
//import net.pointofviews.curation.exception.CurationException;
//import net.pointofviews.curation.repository.CurationRepository;
//import net.pointofviews.curation.service.impl.CurationAdminServiceImpl;
//import net.pointofviews.member.domain.Member;
//import net.pointofviews.member.repository.MemberRepository;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.lang.reflect.Field;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//import java.util.Set;
//
//@ExtendWith(MockitoExtension.class)
//class CurationAdminServiceTest {
//
//    @InjectMocks
//    private CurationAdminServiceImpl curationAdminService;
//
//    @Mock
//    private CurationRepository curationRepository;
//
//    @Mock
//    private CurationRedisService curationRedisService;
//
//    @Mock
//    private MemberRepository memberRepository;
//
//    @Nested
//    class SaveCuration {
//
//        @Nested
//        class Success {
//            @Test
//            void 큐레이션_저장_성공() {
//                // given
//                Member admin = mock(Member.class);
//                given(memberRepository.findById(any())).willReturn(Optional.of(admin));
//
//                CreateCurationRequest request = new CreateCurationRequest(
//                        "Theme",
//                        CurationCategory.GENRE,
//                        "Title",
//                        "Description",
//                        LocalDateTime.now(),
//                        Set.of(101L, 102L, 103L)
//                );
//
//                // Create a answer that sets ID when save is called
//                given(curationRepository.save(any(Curation.class)))
//                        .willAnswer(invocation -> {
//                            Curation savedCuration = invocation.getArgument(0);
//                            // Use reflection to set ID
//                            Field idField = savedCuration.getClass().getDeclaredField("id");
//                            idField.setAccessible(true);
//                            idField.set(savedCuration, 1L);
//                            return savedCuration;
//                        });
//
//                // when
//                curationAdminService.saveCuration(admin, request);
//
//                // then
//                verify(curationRepository, times(1)).save(any(Curation.class));
//                verify(curationRedisService, times(1))
//                        .saveMoviesToCuration(eq(1L), argThat(argument ->
//                                argument.containsAll(Set.of(101L, 102L, 103L))));
//            }
//        }
//
//
//    }
//
//    @Nested
//    class SearchCurations {
//
//        @Nested
//        class Success {
//            @Test
//            void 큐레이션_검색_성공() {
//                // given
//                String theme = "Action";
//                CurationCategory category = CurationCategory.GENRE;
//
//                List<Curation> curations = List.of(
//                        Curation.builder().theme("Action Movies").category(category).title("Best Action").description("Top Action Movies").startTime(LocalDateTime.now()).build(),
//                        Curation.builder().theme("Action Favorites").category(category).title("All Time Action").description("Favorite Action Movies").startTime(LocalDateTime.now()).build()
//                );
//
//                given(curationRepository.searchCurations(theme, category)).willReturn(curations);
//
//                // when
//                ReadCurationListResponse response = curationAdminService.searchCurations(theme, category);
//
//                // then
//                assertThat(response.curations()).hasSize(2);
//                assertThat(response.curations().get(0).theme()).isEqualTo("Action Movies");
//                verify(curationRepository, times(1)).searchCurations(theme, category);
//            }
//        }
//    }
//
//    @Nested
//    class UpdateCuration {
//
//        @Nested
//        class Success {
//            @Test
//            void 큐레이션_수정_성공() {
//                // given
//                Long curationId = 1L;
//                CreateCurationRequest request = new CreateCurationRequest(
//                        "Updated Theme",
//                        CurationCategory.DIRECTOR,
//                        "Updated Title",
//                        "Updated Description",
//                        LocalDateTime.now(),
//                        Set.of(201L, 202L)
//                );
//
//                Curation curation = Curation.builder()
//                        .theme("Original Theme")
//                        .category(CurationCategory.GENRE)
//                        .title("Original Title")
//                        .description("Original Description")
//                        .startTime(LocalDateTime.now())
//                        .build();
//
//                given(curationRepository.findById(curationId)).willReturn(Optional.of(curation));
//
//                // when
//                curationAdminService.updateCuration(curationId, request);
//
//                // then
//                verify(curationRedisService, times(1)).updateMoviesToCuration(curationId, request.movieIds());
//                assertThat(curation.getTheme()).isEqualTo(request.theme());
//                assertThat(curation.getTitle()).isEqualTo(request.title());
//            }
//        }
//
//
//        @Nested
//        class Failure {
//            @Test
//            void 존재하지_않는_큐레이션_수정시_CurationNotFound_예외발생() {
//                // given
//                Long curationId = 1L;
//                CreateCurationRequest request = new CreateCurationRequest(
//                        "Updated Theme",
//                        CurationCategory.DIRECTOR,
//                        "Updated Title",
//                        "Updated Description",
//                        LocalDateTime.now(),
//                        Set.of(201L, 202L)
//                );
//
//                given(curationRepository.findById(curationId)).willReturn(Optional.empty());
//
//                // when & then
//                assertThrows(CurationException.class, () -> curationAdminService.updateCuration(curationId, request));
//            }
//        }
//
//    }
//
//    @Nested
//    class DeleteCuration {
//
//        @Nested
//        class Success {
//            @Test
//            void 큐레이션_삭제_성공() {
//                // given
//                Long curationId = 1L;
//                given(curationRepository.existsById(curationId)).willReturn(true);
//
//                // when
//                curationAdminService.deleteCuration(curationId);
//
//                // then
//                verify(curationRepository, times(1)).deleteById(curationId);
//                verify(curationRedisService, times(1)).deleteAllMoviesForCuration(curationId);
//            }
//        }
//
//        @Nested
//        class Failure {
//            @Test
//            void 존재하지_않는_큐레이션_삭제시_CurationNotFound_예외발생() {
//                // given
//                Long curationId = 1L;
//                given(curationRepository.existsById(curationId)).willReturn(false);
//
//                // when & then
//                assertThrows(CurationException.class, () -> curationAdminService.deleteCuration(curationId));
//            }
//        }
//
//    }
//
//
//    @Nested
//    class ReadAllCurations {
//        @Test
//        void 큐레이션_전체_조회_성공() {
//            // given
//            List<ReadAdminAllCurationResponse> mockCurations = List.of(
//                    new ReadAdminAllCurationResponse(1L, "Title1", LocalDateTime.now()),
//                    new ReadAdminAllCurationResponse(2L, "Title2", LocalDateTime.now())
//            );
//
//            given(curationRepository.findAllCurations()).willReturn(mockCurations);
//
//            // when
//            ReadAdminAllCurationListResponse response = curationAdminService.readAllCurations();
//
//            // then
//            assertThat(response.curations()).hasSize(2);
//            assertThat(response.curations().get(0).title()).isEqualTo("Title1");
//            assertThat(response.curations().get(1).title()).isEqualTo("Title2");
//
//            verify(curationRepository).findAllCurations();
//        }
//    }
//
//    @Nested
//    class ReadCurationDetail {
//        @Test
//        void 큐레이션_상세_조회_성공() {
//            // given
//            Long curationId = 1L;
//
//            ReadAdminCurationResponse mockCuration = new ReadAdminCurationResponse(
//                    curationId, "Theme", CurationCategory.ACTOR, "Title", "Description", LocalDateTime.now()
//            );
//
//            Set<Long> mockMovieIds = Set.of(101L, 102L);
//
//            List<ReadAdminCurationMovieResponse> mockMovies = List.of(
//                    new ReadAdminCurationMovieResponse(1L, "Movie1", LocalDate.now()),
//                    new ReadAdminCurationMovieResponse( 2L,"Movie2", LocalDate.now())
//            );
//
//            given(curationRepository.findCurationDetailById(curationId)).willReturn(Optional.of(mockCuration));
//            given(curationRedisService.readMoviesForCuration(curationId)).willReturn(mockMovieIds);
//            given(curationRepository.findMoviesByIds(mockMovieIds)).willReturn(mockMovies);
//
//            // when
//            ReadAdminCurationDetailResponse response = curationAdminService.readCurationDetail(curationId);
//
//            // then
//            assertThat(response.readAdminCurationResponse().title()).isEqualTo("Title");
//            assertThat(response.readAdminCurationMovieResponseList()).hasSize(2);
//            assertThat(response.readAdminCurationMovieResponseList().get(0).title()).isEqualTo("Movie1");
//
//            verify(curationRepository).findCurationDetailById(curationId);
//            verify(curationRedisService).readMoviesForCuration(curationId);
//            verify(curationRepository).findMoviesByIds(mockMovieIds);
//        }
//
//        @Test
//        void 큐레이션_상세_조회_실패_큐레이션_ID_없음() {
//            // given
//            Long curationId = 1L;
//
//            given(curationRepository.findCurationDetailById(curationId)).willReturn(Optional.empty());
//
//            // when & then
//            assertThatThrownBy(() -> curationAdminService.readCurationDetail(curationId))
//                    .isInstanceOf(CurationException.class)
//                    .hasMessageContaining( "큐레이션(Id: 1)이 존재하지 않습니다.");
//
//            verify(curationRepository).findCurationDetailById(curationId);
//        }
//
//        @Test
//        void 큐레이션_상세_조회_성공_영화_ID_없음() {
//            // given
//            Long curationId = 1L;
//
//            ReadAdminCurationResponse mockCuration = new ReadAdminCurationResponse(
//                    curationId, "Theme", CurationCategory.ACTOR, "Title", "Description", LocalDateTime.now()
//            );
//
//            given(curationRepository.findCurationDetailById(curationId)).willReturn(Optional.of(mockCuration));
//            given(curationRedisService.readMoviesForCuration(curationId)).willReturn(Collections.emptySet());
//
//            // when
//            ReadAdminCurationDetailResponse response = curationAdminService.readCurationDetail(curationId);
//
//            // then
//            assertThat(response.readAdminCurationResponse().title()).isEqualTo("Title");
//            assertThat(response.readAdminCurationMovieResponseList()).isEmpty();
//
//            verify(curationRepository).findCurationDetailById(curationId);
//            verify(curationRedisService).readMoviesForCuration(curationId);
//        }
//    }
//
//}