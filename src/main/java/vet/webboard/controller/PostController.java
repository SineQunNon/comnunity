package vet.webboard.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vet.webboard.dto.request.PostCreateRequest;
import vet.webboard.dto.response.PostResponse;
import vet.webboard.service.PostService;

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
}
