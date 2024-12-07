package net.pointofviews.movie.service;

import net.pointofviews.people.domain.People;

public interface MoviePeopleService {
    People savePeopleIfNotExists(People people);
}
