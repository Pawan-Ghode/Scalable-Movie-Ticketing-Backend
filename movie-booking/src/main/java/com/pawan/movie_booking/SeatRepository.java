package com.pawan.moviebooking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository
        extends JpaRepository<Seat, Integer> {

    List<Seat> findAllByIdIn(List<Integer> ids);
}
