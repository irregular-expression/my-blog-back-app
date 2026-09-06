package ru.irrexp.practicum.model;

import lombok.Data;

@Data
public class Comment {

    private Integer id;
    private String content;
    private Integer postId;

}
