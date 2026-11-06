package com.pawan.moviebooking;

import java.time.LocalDateTime;
import java.util.List;

public record BookingResponse(
        Integer bookingId,
        Integer showId,
        List<String> seatNumbers,
        Integer totalAmount,
        String status,
        LocalDateTime createdAt
) {
}
