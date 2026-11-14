
package com.pawan.moviebooking;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;

    public MovieService(
            MovieRepository movieRepository,
            MovieMapper movieMapper) {
        this.movieRepository = movieRepository;
        this.movieMapper = movieMapper;
    }

    public List<Movie> getMovies() {
        return movieRepository.findAll();
    }

    public Movie getMovieById(int id) {
        return movieRepository.findById(id).orElse(null);
    }

    public Movie addMovie(MovieRequest request) {
        Movie movie = movieMapper.toMovie(request);
        return movieRepository.save(movie);
    }

    @Transactional
    public Movie updateMovie(int id, MovieRequest request) {
        Movie existingMovie = getMovieById(id);

        if (existingMovie == null) {
            return null;
        }

        movieMapper.updateMovie(existingMovie, request);
        return movieRepository.save(existingMovie);
    }

    public boolean deleteMovie(int id) {
        if (!movieRepository.existsById(id)) {
            return false;
        }

        movieRepository.deleteById(id);
        return true;
    }

    public List<Movie> searchMoviesByTitle(String title) {
    return movieRepository.findByTitleContainingIgnoreCase(title);
}

public List<Movie> getMoviesByMaxPrice(Integer maxPrice) {
    return movieRepository.findByPriceLessThanEqual(maxPrice);
}

    @Transactional
    public Movie updateMoviePrice(int id, Integer price) {
        Movie existingMovie = getMovieById(id);

        if (existingMovie == null) {
            return null;
        }

        existingMovie.setPrice(price);
        return movieRepository.save(existingMovie);
    }
}
