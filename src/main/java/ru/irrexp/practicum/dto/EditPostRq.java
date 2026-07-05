package ru.irrexp.practicum.dto;

import java.util.List;

public record EditPostRq(Integer id,
                         String title,
                         String text,
                         List<String> tags) {

}
