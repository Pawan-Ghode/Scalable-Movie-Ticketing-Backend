package com.pawan.moviebooking;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response containing the confirmed booking details")
public record BookingResponse(

        @Schema(description = "Unique booking ID", example = "5")
        Integer bookingId,

        @Schema(description = "ID of the booked show", example = "2")
        Integer showId,

        @Schema(
                description = "Seat numbers included in this booking",
                example = "[\"A1\", \"A2\"]"
        )
        List<String> seatNumbers,

        @Schema(description = "Total booking amount", example = "500")
        Integer totalAmount,

        @Schema(description = "Current booking status", example = "CONFIRMED")
        String status,

        @Schema(
                description = "Date and time when booking was created",
                example = "2026-11-12T09:30:00"
        )
        LocalDateTime createdAt

) {}
