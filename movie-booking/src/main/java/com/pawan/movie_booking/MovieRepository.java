package com.pawan.moviebooking;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Integer> {

    List<Movie> findByTitleContainingIgnoreCase(String title);

    List<Movie> findByPriceLessThanEqual(Integer maxPrice);
}
