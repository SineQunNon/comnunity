package vet.webboard.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vet.webboard.dto.request.PostCreateRequest;
import vet.webboard.dto.request.PostUpdateRequest;
import vet.webboard.dto.response.PostDetailResponse;
import vet.webboard.dto.response.PostResponse;
import vet.webboard.service.PostService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @RequestBody @Valid PostCreateRequest request,
            @RequestParam Long memberId) {
        PostResponse response = postService.createPost(request, memberId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostDetailResponse> updatePost(
            @PathVariable Long postId,
            @RequestBody @Valid PostUpdateRequest request,
            @RequestParam Long memberId) {
        PostDetailResponse response = postService.updatePost(postId, request, memberId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            @RequestParam Long memberId) {
        postService.deletePost(postId, memberId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<PostResponse>> findPosts() {
        List<PostResponse> response = postService.findPosts();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailResponse> findPost(@PathVariable Long postId) {
        PostDetailResponse response = postService.findPost(postId);
        return ResponseEntity.ok(response);
    }
}
