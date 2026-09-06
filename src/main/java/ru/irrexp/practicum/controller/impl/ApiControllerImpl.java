package ru.irrexp.practicum.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.irrexp.practicum.controller.ApiController;
import ru.irrexp.practicum.dto.CommentDto;
import ru.irrexp.practicum.dto.CreateCommentRq;
import ru.irrexp.practicum.dto.CreatePostRq;
import ru.irrexp.practicum.dto.PostDto;
import ru.irrexp.practicum.dto.PostsPageDto;
import ru.irrexp.practicum.dto.UpdatePostRq;
import ru.irrexp.practicum.service.CommentService;
import ru.irrexp.practicum.service.ImageService;
import ru.irrexp.practicum.service.LikeService;
import ru.irrexp.practicum.service.PostService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ApiControllerImpl implements ApiController {

    private final PostService postService;
    private final ImageService imageService;
    private final LikeService likeService;
    private final CommentService commentService;

    @Override
    public PostsPageDto searchPosts(String query, Integer pageNumber, Integer pageSize) {
        return postService.search(query, pageNumber, pageSize);
    }

    @Override
    public ResponseEntity<PostDto> getPost(Integer postId) {
        return postService.find(postId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public PostDto addPost(CreatePostRq postRq) {
        return postService.create(postRq);
    }

    @Override
    public ResponseEntity<PostDto> editPost(Integer postId, UpdatePostRq updatePostRq) {
        return postService.update(postId, updatePostRq)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Void> deletePost(Integer postId) {
        return postService.delete(postId) ?
                ResponseEntity.ok().build() :
                ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<Void> addLike(Integer postId) {
        return likeService.like(postId) ?
                ResponseEntity.ok().build() :
                ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<Void> loadImage(Integer postId, MultipartFile multipartFile) {
        return imageService.load(postId, multipartFile) ?
                ResponseEntity.ok().build() :
                ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<byte[]> getImage(Integer postId) {
        return imageService.getByPostId(postId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public List<CommentDto> getComments(Integer postId) {
        return commentService.getAllByPostId(postId);
    }

    @Override
    public ResponseEntity<CommentDto> getComment(Integer postId, Integer commentId) {
        return commentService.find(postId, commentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public CommentDto createComment(Integer postId, CreateCommentRq createCommentRq) {
        return commentService.create(postId, createCommentRq);
    }

    @Override
    public ResponseEntity<CommentDto> editComment(Integer postId, Integer commentId, CommentDto comment) {
        return commentService.update(postId, commentId, comment)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Void> deleteComment(Integer postId, Integer commentId) {
        return commentService.delete(postId, commentId) ?
                ResponseEntity.ok().build() :
                ResponseEntity.notFound().build();
    }

}
