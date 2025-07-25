package com.petcare.petcareapp.repository;

import com.petcare.petcareapp.domain.Comment;
import com.petcare.petcareapp.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostOrderByCreatedAtAsc(Post post); // Or Desc depending on desired order
    List<Comment> findByPostIdOrderByCreatedAtAsc(Long postId);
    // Optional: findByPostAndId for specific comment under a post, though findById should be sufficient with service check
}
