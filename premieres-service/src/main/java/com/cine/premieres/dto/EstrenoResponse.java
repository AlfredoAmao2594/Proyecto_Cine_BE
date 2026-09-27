package com.cine.premieres.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstrenoResponse {
    private UUID id;
    private String title;
    private String description;
    private String imageUrl;
    private LocalDate releaseDate;
}