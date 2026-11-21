package com.pawan.moviebooking;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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

    // Existing methods retained
    public List<Movie> getMovies() {
        return movieRepository.findAll();
    }

    public Movie getMovieById(int id) {
        return movieRepository.findById(id).orElse(null);
    }

    // Redis cache: all movies
    @Cacheable(cacheNames = "movies", key = "'all'")
    public List<MovieResponse> getCachedMovies() {
        return movieRepository.findAll()
                .stream()
                .map(movieMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Redis cache: individual movie
    @Cacheable(
            cacheNames = "movieById",
            key = "#id",
            unless = "#result == null"
    )
    public MovieResponse getCachedMovieById(int id) {
        return movieRepository.findById(id)
                .map(movieMapper::toResponse)
                .orElse(null);
    }

    // Clear both caches after successful creation
    @Caching(evict = {
            @CacheEvict(cacheNames = "movies", allEntries = true),
            @CacheEvict(cacheNames = "movieById", allEntries = true)
    })
    public Movie addMovie(MovieRequest request) {
        Movie movie = movieMapper.toMovie(request);
        return movieRepository.save(movie);
    }

    // Clear caches after successful update
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = "movies", allEntries = true),
            @CacheEvict(cacheNames = "movieById", allEntries = true)
    })
    public Movie updateMovie(int id, MovieRequest request) {
        Movie existingMovie = getMovieById(id);

        if (existingMovie == null) {
            return null;
        }

        movieMapper.updateMovie(existingMovie, request);
        return movieRepository.save(existingMovie);
    }

    // Clear caches after deletion
    @Caching(evict = {
            @CacheEvict(cacheNames = "movies", allEntries = true),
            @CacheEvict(cacheNames = "movieById", allEntries = true)
    })
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

    // Clear caches after price update
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = "movies", allEntries = true),
            @CacheEvict(cacheNames = "movieById", allEntries = true)
    })
    public Movie updateMoviePrice(int id, Integer price) {
        Movie existingMovie = getMovieById(id);

        if (existingMovie == null) {
            return null;
        }

        existingMovie.setPrice(price);
        return movieRepository.save(existingMovie);
    }
}
