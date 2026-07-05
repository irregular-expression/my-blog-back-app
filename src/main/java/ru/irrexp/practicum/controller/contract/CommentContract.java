package ru.irrexp.practicum.controller.contract;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.irrexp.practicum.dto.CommentDto;
import ru.irrexp.practicum.dto.CreateCommentRq;

import java.util.List;

public interface CommentContract {

    @GetMapping("/posts/{postId}/comments")
    @ResponseBody
    List<CommentDto> getComments(@PathVariable("postId") Integer postId);

    @GetMapping("/posts/{postId}/comments/{commentId}")
    ResponseEntity<CommentDto> getComment(@PathVariable("postId") Integer postId,
                                          @PathVariable("commentId") Integer commentId);

    @PostMapping("/posts/{postId}/comments")
    @ResponseBody
    CommentDto createComment(@PathVariable("postId") Integer postId,
                             @RequestBody CreateCommentRq createCommentRq);

    @PutMapping("/posts/{postId}/comments/{commentId}")
    ResponseEntity<CommentDto> editComment(@PathVariable("postId") Integer postId,
                                           @PathVariable("commentId") Integer commentId,
                                           @RequestBody CommentDto post);

    @DeleteMapping("/posts/{postId}/comments/{commentId}")
    ResponseEntity<Void> deleteComment(@PathVariable("postId") Integer postId,
                                       @PathVariable("commentId") Integer commentId);

}
