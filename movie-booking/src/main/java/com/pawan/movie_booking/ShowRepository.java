package com.pawan.moviebooking;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

public interface ShowRepository extends JpaRepository<Show, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Show s WHERE s.id = :showId")
    Optional<Show> findByIdForUpdate(
            @Param("showId") Integer showId);
}
