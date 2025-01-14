package net.pointofviews.people.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.pointofviews.movie.domain.MovieCast;
import net.pointofviews.movie.domain.MovieCrew;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class People {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String imageUrl;

    private Integer tmdbId;

    @OneToMany(mappedBy = "people")
    private final List<MovieCrew> crews = new ArrayList<>();

    @OneToMany(mappedBy = "people")
    private final List<MovieCast> casts = new ArrayList<>();

    @Builder
    private People(String name, String imageUrl, Integer tmdbId) {
        Assert.notNull(name, "name must not be null");
        this.name = name;
        this.imageUrl = imageUrl;
        this.tmdbId = tmdbId;
    }

    public void addCrew(MovieCrew crew) {
        crews.add(crew);
        crew.updatePeople(this);
    }

    public void addCast(MovieCast cast) {
        casts.add(cast);
        cast.updatePeople(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        People people = (People) o;
        return Objects.equals(getId(), people.getId()) && Objects.equals(getName(), people.getName()) && Objects.equals(getImageUrl(), people.getImageUrl()) && Objects.equals(getTmdbId(), people.getTmdbId()) && getCrews().equals(people.getCrews()) && getCasts().equals(people.getCasts());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getId());
        result = 31 * result + Objects.hashCode(getName());
        result = 31 * result + Objects.hashCode(getImageUrl());
        result = 31 * result + Objects.hashCode(getTmdbId());
        result = 31 * result + getCrews().hashCode();
        result = 31 * result + getCasts().hashCode();
        return result;
    }
}