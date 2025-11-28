package vet.webboard.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vet.webboard.dto.response.PostLikeResponse;
import vet.webboard.service.PostLikeService;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostLikeController {
    private final PostLikeService postLikeService;

    @PostMapping("/{postId}/like")
    public ResponseEntity<PostLikeResponse> likePost(@PathVariable Long postId,
                                                     @RequestParam Long memberId) {
        PostLikeResponse response = postLikeService.likePost(postId, memberId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{postId}/like")
    public ResponseEntity<PostLikeResponse> unlikePost(@PathVariable Long postId,
                                           @RequestParam Long memberId) {
        PostLikeResponse response = postLikeService.unlikePost(postId, memberId);
        return ResponseEntity.ok(response);
    }
}
