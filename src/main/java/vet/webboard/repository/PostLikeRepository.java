package vet.webboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vet.webboard.domain.PostLike;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    boolean existsByPostIdAndMemberId(Long postId, Long memberId);
}
