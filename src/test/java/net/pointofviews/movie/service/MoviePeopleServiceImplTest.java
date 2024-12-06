package net.pointofviews.movie.service;

import net.pointofviews.movie.service.impl.MoviePeopleServiceImpl;
import net.pointofviews.people.domain.People;
import net.pointofviews.people.repository.PeopleRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MoviePeopleServiceImplTest {

    @InjectMocks
    private MoviePeopleServiceImpl moviePeopleServiceImpl;

    @Mock
    private PeopleRepository peopleRepository;

    @Nested
    class SavePeopleIfNotExists {

        @Nested
        class Success {

            @Test
            void 같은_이름을_가진_People_이_부재할_때_저장() {
                // given
                String name = "people";
                String imageUrl = "imageUrl";
                Integer tmdbId = 100;

                People people = People.builder()
                        .name(name)
                        .imageUrl(imageUrl)
                        .tmdbId(tmdbId)
                        .build();

                People savedPeople = spy(people);
                given(peopleRepository.findByTmdbId(tmdbId)).willReturn(Optional.empty());
                given(peopleRepository.save(people)).willReturn(savedPeople);
                given(savedPeople.getId()).willReturn(1L);

                // when
                People result = moviePeopleServiceImpl.savePeopleIfNotExists(people);

                // then
                assertThat(result).isNotNull();
                assertThat(result.getId()).isEqualTo(1);
                assertThat(result.getName()).isEqualTo(name);
                assertThat(result.getImageUrl()).isEqualTo(imageUrl);
                verify(peopleRepository).save(people);
            }

            @Test
            void 같은_이름을_가진_People_이_존재할_때_조회만_실행() {
                // given
                String name = "people";
                String imageUrl = "imageUrl";
                Integer tmdbId = 100;

                People people = People.builder()
                        .name(name)
                        .imageUrl(imageUrl)
                        .tmdbId(tmdbId)
                        .build();

                People existingPeople = spy(people);
                given(peopleRepository.findByTmdbId(tmdbId)).willReturn(Optional.of(existingPeople));
                given(existingPeople.getId()).willReturn(1L);

                // when
                People result = moviePeopleServiceImpl.savePeopleIfNotExists(people);

                // then
                assertThat(result).isNotNull();
                assertThat(result.getId()).isEqualTo(1);
                assertThat(result.getName()).isEqualTo(name);
                assertThat(result.getImageUrl()).isEqualTo(imageUrl);
                verify(peopleRepository, never()).save(any(People.class));
            }
        }
    }
}
