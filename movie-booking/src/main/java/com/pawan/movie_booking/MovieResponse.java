package com.pawan.moviebooking;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Movie details returned by the API")
public record MovieResponse(

        @Schema(description = "Unique movie ID", example = "3")
        int id,

        @Schema(description = "Movie title", example = "Inception")
        String title,

        @Schema(description = "Movie ticket price", example = "250")
        int price

) {}
