package net.pointofviews.movie.service;

import net.pointofviews.country.domain.Country;
import net.pointofviews.country.repository.CountryRepository;
import net.pointofviews.movie.service.impl.MovieCountryServiceImpl;
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
class MovieCountryServiceImplTest {

    @InjectMocks
    private MovieCountryServiceImpl movieCountryServiceImpl;

    @Mock
    private CountryRepository countryRepository;

    @Nested
    class SaveMovieCountries {

        @Nested
        class Success {

            @Test
            void 같은_이름을_가진_Country_가_부재할_때_저장한다() {
                // given
                String countryName = "대한민국";
                Country country = new Country(countryName);
                Country savedCountry = spy(country);

                given(countryRepository.findByName(countryName)).willReturn(Optional.empty());
                given(countryRepository.save(country)).willReturn(savedCountry);
                given(savedCountry.getId()).willReturn(1L);

                // when
                Country saved = movieCountryServiceImpl.saveMovieCountries(country);

                // then
                assertThat(saved.getName()).isEqualTo(countryName);
                assertThat(country.getId()).isNull();
                assertThat(saved.getId()).isEqualTo(1);
            }

            @Test
            void 같은_이름을_가진_Country_가_존재할_때_조회한다() {
                // given
                String countryName = "대한민국";
                Country country = new Country(countryName);
                Country savedCountry = spy(country);

                given(countryRepository.findByName(countryName)).willReturn(Optional.of(savedCountry));
                given(savedCountry.getId()).willReturn(1L);

                // when
                Country saved = movieCountryServiceImpl.saveMovieCountries(country);

                // then
                assertThat(saved.getName()).isEqualTo(countryName);
                assertThat(country.getId()).isNull();
                assertThat(saved.getId()).isEqualTo(1);
                verify(countryRepository, never()).save(country);
            }
        }
    }
}