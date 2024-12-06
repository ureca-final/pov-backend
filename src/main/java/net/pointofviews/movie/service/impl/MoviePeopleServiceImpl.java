package net.pointofviews.movie.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pointofviews.movie.service.MoviePeopleService;
import net.pointofviews.people.domain.People;
import net.pointofviews.people.repository.PeopleRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MoviePeopleServiceImpl implements MoviePeopleService {
    private final PeopleRepository peopleRepository;

    public People savePeopleIfNotExists(People people) {
        return peopleRepository.findByTmdbId(people.getTmdbId())
                .orElseGet(() -> peopleRepository.save(people));
    }
}
