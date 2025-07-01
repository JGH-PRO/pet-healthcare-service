package com.petcare.petcareapp.service;

import com.petcare.petcareapp.domain.Post;
import com.petcare.petcareapp.domain.PostLike;
import com.petcare.petcareapp.domain.User;
import com.petcare.petcareapp.repository.PostLikeRepository;
import com.petcare.petcareapp.repository.PostRepository;
import com.petcare.petcareapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LikeService {

    @Autowired private PostLikeRepository postLikeRepository;
    @Autowired private PostRepository postRepository;
    @Autowired private UserRepository userRepository;

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Transactional
    public void likePost(Long postId, String username) {
        User user = getUserByUsername(username);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        if (postLikeRepository.existsByPostAndUser(post, user)) {
            // User has already liked this post, could throw an exception or just do nothing
            // For idempotency, doing nothing is fine. Or throw new RuntimeException("Post already liked by this user.");
            return;
        }

        PostLike like = new PostLike(post, user);
        postLikeRepository.save(like);
        // Note: The Post entity's like collection is not explicitly managed here,
        // as it's a bidirectional relationship. The count in PostDto relies on PostLikeRepository or post.getLikes().size().
        // If post.getLikes().add(like) was used, ensure PostLike's post field is also set.
        // The current PostLike constructor and Post.addLike handle this if called.
        // For simplicity, we directly save PostLike. The Post entity's @OneToMany will reflect this.
    }

    @Transactional
    public void unlikePost(Long postId, String username) {
        User user = getUserByUsername(username);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        PostLike like = postLikeRepository.findByPostAndUser(post, user)
                .orElseThrow(() -> new RuntimeException("Post not liked by this user."));

        postLikeRepository.delete(like);
        // Similar to likePost, Post entity's collection will reflect this change.
    }

    @Transactional(readOnly = true)
    public long getLikeCountForPost(Long postId) {
        // Post post = postRepository.findById(postId)
        //         .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));
        // return postLikeRepository.countByPost(post);
        // Or directly by postId if preferred and available (added to repo)
        return postLikeRepository.countByPostId(postId);
    }

    @Transactional(readOnly = true)
    public boolean hasUserLikedPost(Long postId, String username) {
        User user = getUserByUsername(username);
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) return false; // Post doesn't exist
        return postLikeRepository.existsByPostAndUser(post, user);
    }
}
