package com.petcare.petcareapp.service.admin;

import com.petcare.petcareapp.domain.Post;
import com.petcare.petcareapp.dto.post.PostDto; // Reusing existing PostDto
import com.petcare.petcareapp.repository.PostRepository;
// No need for UserRepository here unless we were changing authors, which is not planned.
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminPostService {

    @Autowired
    private PostRepository postRepository;

    @Transactional(readOnly = true)
    public Page<PostDto> getAllPosts(Pageable pageable) {
        return postRepository.findAllByOrderByCreatedAtDesc(pageable).map(this::mapEntityToPostDto);
    }

    @Transactional(readOnly = true)
    public PostDto getPostById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));
        return mapEntityToPostDto(post);
    }

    @Transactional
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));
        // Admins can delete any post. No ownership check needed here.
        postRepository.delete(post);
    }

    private PostDto mapEntityToPostDto(Post post) {
        if (post == null) return null;
        return new PostDto(
                post.getId(),
                post.getContent(),
                post.getImageUrl(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                post.getAuthor() != null ? post.getAuthor().getId() : null, // Null check for author
                post.getAuthor() != null ? post.getAuthor().getUsername() : null, // Null check for author
                post.getLikes() != null ? post.getLikes().size() : 0, // Null check for likes
                post.getComments() != null ? post.getComments().size() : 0 // Null check for comments
        );
    }
}
