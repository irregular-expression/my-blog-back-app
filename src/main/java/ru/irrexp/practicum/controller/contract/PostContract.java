package ru.irrexp.practicum.controller.contract;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.irrexp.practicum.dto.CreatePostRq;
import ru.irrexp.practicum.dto.PostDto;
import ru.irrexp.practicum.dto.PostsPageDto;
import ru.irrexp.practicum.dto.UpdatePostRq;

public interface PostContract {
    @GetMapping("/posts")
    @ResponseBody
    PostsPageDto searchPosts(@RequestParam("search") String search,
                             @RequestParam("pageNumber") Integer pageNumber,
                             @RequestParam("pageSize") Integer pageSize);

    @PostMapping("/post/{postId}")
    ResponseEntity<PostDto> getPost(@PathVariable("postId") Integer postId);

    @PostMapping("/posts")
    @ResponseBody
    PostDto addPost(@RequestBody CreatePostRq postRq);

    @PutMapping("/posts/{postId}")
    ResponseEntity<PostDto> editPost(@PathVariable("postId") Integer postId, @RequestBody UpdatePostRq updatePostRq);

    @DeleteMapping("/posts/{postId}")
    ResponseEntity<Void> deletePost(@PathVariable("postId") Integer postId);

    @PostMapping("/posts/{postId}/likes")
    ResponseEntity<Void> addLike(@PathVariable("postId") Integer postId);

}
