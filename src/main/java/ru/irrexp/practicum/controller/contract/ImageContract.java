package ru.irrexp.practicum.controller.contract;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

public interface ImageContract {

    @PutMapping(value = "/posts/{postId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<Void> loadImage(@PathVariable("postId") Integer postId,
                                   @RequestParam("file") MultipartFile multipartFile);

    @GetMapping(value = "/posts/{postId}/image", produces = MediaType.IMAGE_JPEG_VALUE)
    ResponseEntity<byte[]> getImage(@PathVariable("postId") Integer postId);
}
