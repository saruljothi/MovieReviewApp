package com.mobiquity.movieReviewApp.Entity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "movie")
public class Movie {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private String releaseDate;
  private String description;
  private int runtime;

  private String metacriticScore;
  private String rottenTomatoes;
  private String imdbRating;

  @ManyToMany(mappedBy = "movieWatchlist")
  private Set<UserProfile> users = new HashSet<>();

  @Override
  public boolean equals(Object o) {
    if(this == o){
      return true;
    }
    if(o == null || getClass() != o.getClass()){
      return false;
    }
    Movie movie = (Movie) o;
    return Objects.equals(this.name, movie.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.name);
  }

}
