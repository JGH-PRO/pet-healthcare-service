package com.petcare.petcareapp.repository;

import com.petcare.petcareapp.domain.Post;
import com.petcare.petcareapp.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query; // For future optimized DTO queries
import org.springframework.stereotype.Repository;

import java.util.List;
// import java.util.Optional; // For future optimized DTO queries

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);
    List<Post> findByAuthorOrderByCreatedAtDesc(User author);

    // For fetching PostDto with counts directly (more advanced, for later optimization)
    // @Query("SELECT new com.petcare.petcareapp.dto.post.PostDto(p.id, p.content, p.imageUrl, p.createdAt, p.updatedAt, p.author.id, p.author.username, SIZE(p.likes), SIZE(p.comments)) FROM Post p WHERE p.id = :postId")
    // Optional<PostDto> findPostDtoById(Long postId);

    // @Query("SELECT new com.petcare.petcareapp.dto.post.PostDto(p.id, p.content, p.imageUrl, p.createdAt, p.updatedAt, p.author.id, p.author.username, SIZE(p.likes), SIZE(p.comments)) FROM Post p ORDER BY p.createdAt DESC")
    // Page<PostDto> findAllPostDtosByOrderByCreatedAtDesc(Pageable pageable);
}
