
package com.pawan.moviebooking;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
public class HelloController {

    private final MovieService movieService;
    private final MovieMapper movieMapper;

    public HelloController(
            MovieService movieService,
            MovieMapper movieMapper) {
        this.movieService = movieService;
        this.movieMapper = movieMapper;
    }

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello Pawan! Spring Boot is working.";
    }

    @GetMapping("/movies")
    public List<MovieResponse> getMovies() {
        return movieService.getMovies()
                .stream()
                .map(movieMapper::toResponse)
                .toList();
    }

    @GetMapping("/movies/{id}")
    public ResponseEntity<?> getMovieById(@PathVariable int id) {
        Movie movie = movieService.getMovieById(id);

        if (movie == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiError(404, "Movie not found"));
        }

        return ResponseEntity.ok(movieMapper.toResponse(movie));
    }

    @PostMapping("/movies")
    public ResponseEntity<MovieResponse> addMovie(
            @Valid @RequestBody MovieRequest request) {
        Movie savedMovie = movieService.addMovie(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(movieMapper.toResponse(savedMovie));
    }

    @PutMapping("/movies/{id}")
    public ResponseEntity<?> updateMovie(
            @PathVariable int id,
            @Valid @RequestBody MovieRequest request) {
        Movie updatedMovie = movieService.updateMovie(id, request);

        if (updatedMovie == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiError(404, "Movie not found"));
        }

        return ResponseEntity.ok(movieMapper.toResponse(updatedMovie));
    }

    @DeleteMapping("/movies/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable int id) {
        boolean deleted = movieService.deleteMovie(id);

        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/movies/{id}")
    public ResponseEntity<?> updateMoviePrice(
            @PathVariable int id,
            @RequestBody UpdateMoviePriceRequest request) {

        if (request.price() == null || request.price() < 0) {
            return ResponseEntity.badRequest()
                    .body(new ApiError(400,
                            "Price must be zero or greater"));
        }

        Movie updatedMovie =
                movieService.updateMoviePrice(id, request.price());

        if (updatedMovie == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiError(404, "Movie not found"));
        }

        return ResponseEntity.ok(movieMapper.toResponse(updatedMovie));
    }

    @GetMapping("/movies/search")
public ResponseEntity<?> searchMovies(@RequestParam String title) {

    if (title.isBlank()) {
        return ResponseEntity.badRequest()
                .body(new ApiError(400, "Search title cannot be blank"));
    }

    List<MovieResponse> results = movieService
            .searchMoviesByTitle(title)
            .stream()
            .map(movieMapper::toResponse)
            .toList();

    return ResponseEntity.ok(results);
}

@GetMapping("/movies/filter")
public ResponseEntity<?> filterMovies(@RequestParam Integer maxPrice) {

    if (maxPrice < 0) {
        return ResponseEntity.badRequest()
                .body(new ApiError(400, "Maximum price cannot be negative"));
    }

    List<MovieResponse> results = movieService
            .getMoviesByMaxPrice(maxPrice)
            .stream()
            .map(movieMapper::toResponse)
            .toList();

    return ResponseEntity.ok(results);
  }
}
