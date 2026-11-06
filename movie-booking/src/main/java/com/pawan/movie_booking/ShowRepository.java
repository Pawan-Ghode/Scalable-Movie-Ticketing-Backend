package com.pawan.moviebooking;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ShowRepository
        extends JpaRepository<Show, Integer> {
}

