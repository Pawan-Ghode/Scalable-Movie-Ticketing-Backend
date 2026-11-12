package com.pawan.moviebooking;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request payload for creating or updating a movie")
public record MovieRequest(

        @Schema(
                description = "Movie title",
                example = "Inception",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank
        String title,

        @Schema(
                description = "Movie ticket price",
                example = "250",
                minimum = "0",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @PositiveOrZero
        int price

) {}
