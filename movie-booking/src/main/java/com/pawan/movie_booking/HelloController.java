
package com.pawan.moviebooking;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@Tag(name = "Movies", description = "APIs for managing and searching movies")
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
    @Operation(summary = "Health check", description = "Checks whether the application is running")
    @ApiResponse(responseCode = "200", description = "Application is running")
    public String sayHello() {
        return "Hello Pawan! Spring Boot is working.";
    }

    @GetMapping("/movies")
    @Operation(summary = "Get all movies", description = "Returns the list of all available movies")
    @ApiResponse(responseCode = "200", description = "Movies retrieved successfully")
    public List<MovieResponse> getMovies() {
        return movieService.getMovies()
                .stream()
                .map(movieMapper::toResponse)
                .toList();
    }

    @GetMapping("/movies/{id}")
    @Operation(summary = "Get movie by ID", description = "Returns details of a specific movie")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Movie found"),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    public ResponseEntity<?> getMovieById(
            @Parameter(description = "ID of the movie")
            @PathVariable int id) {

        Movie movie = movieService.getMovieById(id);

        if (movie == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiError(404, "Movie not found"));
        }

        return ResponseEntity.ok(movieMapper.toResponse(movie));
    }

    @PostMapping("/movies")
    @Operation(summary = "Add a movie", description = "Creates a new movie")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Movie created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid movie request")
    })
    public ResponseEntity<MovieResponse> addMovie(
            @Valid @RequestBody MovieRequest request) {

        Movie savedMovie = movieService.addMovie(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(movieMapper.toResponse(savedMovie));
    }

    @PutMapping("/movies/{id}")
    @Operation(summary = "Update a movie", description = "Replaces the movie details for the given ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Movie updated successfully"),
            @ApiResponse(responseCode = "404", description = "Movie not found"),
            @ApiResponse(responseCode = "400", description = "Invalid movie request")
    })
    public ResponseEntity<?> updateMovie(
            @Parameter(description = "ID of the movie")
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
    @Operation(summary = "Delete a movie", description = "Deletes a movie using its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Movie deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    public ResponseEntity<Void> deleteMovie(
            @Parameter(description = "ID of the movie")
            @PathVariable int id) {

        boolean deleted = movieService.deleteMovie(id);

        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/movies/{id}")
    @Operation(summary = "Update movie price", description = "Partially updates the price of a movie")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Movie price updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid price"),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    public ResponseEntity<?> updateMoviePrice(
            @Parameter(description = "ID of the movie")
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
    @Operation(summary = "Search movies by title", description = "Finds movies whose titles match the supplied search text")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search completed successfully"),
            @ApiResponse(responseCode = "400", description = "Search title cannot be blank")
    })
    public ResponseEntity<?> searchMovies(
            @Parameter(description = "Title text to search for")
            @RequestParam String title) {

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
    @Operation(summary = "Filter movies by maximum price", description = "Returns movies whose price is less than or equal to maxPrice")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Movies filtered successfully"),
            @ApiResponse(responseCode = "400", description = "Maximum price cannot be negative")
    })
    public ResponseEntity<?> filterMovies(
            @Parameter(description = "Maximum allowed movie price")
            @RequestParam Integer maxPrice) {

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
