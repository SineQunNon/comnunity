package vet.webboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vet.webboard.domain.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
}
