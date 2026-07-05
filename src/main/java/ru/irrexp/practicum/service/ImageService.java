package ru.irrexp.practicum.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface ImageService {

    boolean load(Integer postId, MultipartFile multipartFile);

    Optional<byte[]> getByPostId(Integer postId);

}
