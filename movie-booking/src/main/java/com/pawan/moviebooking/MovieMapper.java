
package com.pawan.moviebooking;

import org.springframework.stereotype.Component;

@Component
public class MovieMapper {

    public Movie toMovie(MovieRequest request) {
        Movie movie = new Movie();
        movie.setTitle(request.title());
        movie.setPrice(request.price());
        return movie;
    }

    public void updateMovie(Movie movie, MovieRequest request) {
        movie.setTitle(request.title());
        movie.setPrice(request.price());
    }

    public MovieResponse toResponse(Movie movie) {
        return new MovieResponse(
                movie.getId(),
                movie.getTitle(),
                movie.getPrice()
        );
    }
}
