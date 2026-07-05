package ru.irrexp.practicum.model;

import lombok.Data;

import java.util.Set;

@Data
public class Post {

    private Integer id;
    private String title;
    private String text;
    private Set<String> tags;
    private Integer likesCount;
    private Integer commentsCount;

    public void like() {
        likesCount += 1;
    }

}
