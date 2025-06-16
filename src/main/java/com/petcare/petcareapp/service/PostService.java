package com.petcare.petcareapp.service;

import com.petcare.petcareapp.domain.Post;
import com.petcare.petcareapp.domain.User;
import com.petcare.petcareapp.dto.post.CreatePostRequestDto;
import com.petcare.petcareapp.dto.post.PostDto;
import com.petcare.petcareapp.repository.PostRepository;
import com.petcare.petcareapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostService {

    @Autowired private PostRepository postRepository;
    @Autowired private UserRepository userRepository;

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Transactional
    public PostDto createPost(CreatePostRequestDto requestDto, String username) {
        User author = getUserByUsername(username);
        Post post = new Post();
        post.setAuthor(author);
        post.setContent(requestDto.getContent());
        post.setImageUrl(requestDto.getImageUrl());
        // createdAt and updatedAt are handled by @CreationTimestamp and @UpdateTimestamp

        Post savedPost = postRepository.save(post);
        return mapToDto(savedPost);
    }

    @Transactional(readOnly = true)
    public Page<PostDto> getAllPosts(Pageable pageable) {
        // Using findAll and mapping. For optimized counts, a custom query in repo would be better.
        return postRepository.findAllByOrderByCreatedAtDesc(pageable).map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public PostDto getPostById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));
        return mapToDto(post);
    }

    @Transactional
    public void deletePost(Long postId, String username) {
        User currentUser = getUserByUsername(username);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        // Check if current user is the author or an admin (admin role check needs to be implemented if "ADMIN" role exists)
        if (!post.getAuthor().equals(currentUser) && (currentUser.getRole() == null || !currentUser.getRole().equals("ADMIN"))) {
            throw new AccessDeniedException("User is not authorized to delete this post.");
        }
        postRepository.delete(post);
    }

    private PostDto mapToDto(Post post) {
        return new PostDto(
                post.getId(),
                post.getContent(),
                post.getImageUrl(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                post.getAuthor().getId(),
                post.getAuthor().getUsername(),
                post.getLikes().size(),      // Simple count from collection size
                post.getComments().size()    // Simple count from collection size
        );
    }
}
