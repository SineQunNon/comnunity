package vet.webboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vet.webboard.domain.PostImage;

@Repository
public interface PostImageRepository extends JpaRepository<PostImage, Long> {
}
