package com.pawan.moviebooking;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingSeatRepository
        extends JpaRepository<BookingSeat, Integer> {

    boolean existsByShow_IdAndSeat_Id(
            Integer showId,
            Integer seatId
    );
}

