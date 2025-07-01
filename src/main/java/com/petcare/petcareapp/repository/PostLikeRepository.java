package com.petcare.petcareapp.repository;

import com.petcare.petcareapp.domain.Post;
import com.petcare.petcareapp.domain.PostLike;
import com.petcare.petcareapp.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByPostAndUser(Post post, User user);
    boolean existsByPostAndUser(Post post, User user);
    // Spring Data JPA can automatically provide countByPost(Post post) or countByPostId(Long postId)
    long countByPost(Post post);
    long countByPostId(Long postId);
}
