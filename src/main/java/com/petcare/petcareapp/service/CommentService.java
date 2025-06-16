package com.petcare.petcareapp.service;

import com.petcare.petcareapp.domain.Comment;
import com.petcare.petcareapp.domain.Post;
import com.petcare.petcareapp.domain.User;
import com.petcare.petcareapp.dto.comment.CommentDto;
import com.petcare.petcareapp.dto.comment.CreateCommentRequestDto;
import com.petcare.petcareapp.repository.CommentRepository;
import com.petcare.petcareapp.repository.PostRepository;
import com.petcare.petcareapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired private CommentRepository commentRepository;
    @Autowired private PostRepository postRepository;
    @Autowired private UserRepository userRepository;

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Transactional
    public CommentDto addComment(Long postId, CreateCommentRequestDto requestDto, String username) {
        User author = getUserByUsername(username);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setAuthor(author);
        comment.setContent(requestDto.getContent());
        // createdAt and updatedAt are handled by @CreationTimestamp and @UpdateTimestamp

        Comment savedComment = commentRepository.save(comment);
        return mapToDto(savedComment);
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByPostId(Long postId) {
        // Ensure post exists, though repository method findByPostId will return empty if not.
        if (!postRepository.existsById(postId)) {
             throw new RuntimeException("Post not found with id: " + postId);
        }
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CommentDto getCommentById(Long commentId) { // This method is not used by controller in this iteration but is good for service layer
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + commentId));
        return mapToDto(comment);
    }


    @Transactional
    public void deleteComment(Long postId, Long commentId, String username) {
        User currentUser = getUserByUsername(username);
        Post post = postRepository.findById(postId) // Verify post exists in this context
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + commentId));

        if (comment.getPost() == null || !comment.getPost().getId().equals(post.getId())) { // Added null check for comment.getPost()
            throw new AccessDeniedException("Comment does not belong to the specified post.");
        }

        // Check if current user is the author of the comment or an admin
        // Or if current user is the author of the POST (post authors can often delete comments on their posts)
        boolean isAdmin = currentUser.getRole() != null && currentUser.getRole().equals("ADMIN"); // Added null check for role
        boolean isCommentAuthor = comment.getAuthor().equals(currentUser);
        boolean isPostAuthor = post.getAuthor().equals(currentUser);


        if (!isCommentAuthor && !isAdmin && !isPostAuthor) {
            throw new AccessDeniedException("User is not authorized to delete this comment.");
        }
        commentRepository.delete(comment);
    }

    private CommentDto mapToDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getUpdatedAt(),
                comment.getAuthor().getId(),
                comment.getAuthor().getUsername(),
                comment.getPost().getId()
        );
    }
}
