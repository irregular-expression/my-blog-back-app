package ru.irrexp.practicum.dto;

import lombok.Builder;

import java.util.List;

public record UpdatePostRq(Integer id,
                           String title,
                           String text,
                           List<String> tags) {

    @Builder
    public UpdatePostRq {
    }

}
