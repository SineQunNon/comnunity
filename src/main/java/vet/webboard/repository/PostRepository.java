package vet.webboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vet.webboard.domain.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
}
