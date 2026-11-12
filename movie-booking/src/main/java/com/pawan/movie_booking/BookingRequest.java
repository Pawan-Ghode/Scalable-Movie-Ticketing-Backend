package com.pawan.moviebooking;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request payload for booking movie seats")
public record BookingRequest(

        @Schema(
                description = "ID of the movie show",
                example = "2",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull
        Integer showId,

        @Schema(
                description = "List of seat IDs to book. Must contain at least one seat.",
                example = "[1, 2]",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotEmpty
        List<@NotNull Integer> seatIds

) {}
