package com.petcare.petcareapp.controller;

import com.petcare.petcareapp.dto.post.CreatePostRequestDto;
import com.petcare.petcareapp.dto.post.PostDto;
import com.petcare.petcareapp.service.PostService;
import com.petcare.petcareapp.service.LikeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/posts")
@Tag(name = "Post Management", description = "APIs for creating, viewing, and managing posts in the community feed")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private LikeService likeService;

    @PostMapping
    @Operation(summary = "Create a new post", description = "Creates a new post for the community feed, authored by the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Post created successfully",
                         content = @Content(mediaType = "application/json", schema = @Schema(implementation = PostDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<PostDto> createPost(@Valid @RequestBody CreatePostRequestDto requestDto,
                                              @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        try {
            PostDto createdPost = postService.createPost(requestDto, userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @GetMapping
    @Operation(summary = "List all posts", description = "Retrieves a paginated list of all posts, ordered by creation date (newest first).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of posts",
                         content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    })
    public ResponseEntity<Page<PostDto>> getAllPosts(
            @Parameter(description = "Pagination and sorting parameters") @PageableDefault(size = 10, sort = "createdAt,desc") Pageable pageable) {
        Page<PostDto> posts = postService.getAllPosts(pageable);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{postId}")
    @Operation(summary = "Get a specific post by ID", description = "Retrieves details of a single post by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved post",
                         content = @Content(mediaType = "application/json", schema = @Schema(implementation = PostDto.class))),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    public ResponseEntity<PostDto> getPostById(
            @Parameter(name = "postId", description = "ID of the post to retrieve", required = true) @PathVariable Long postId) {
        try {
            PostDto postDto = postService.getPostById(postId);
            return ResponseEntity.ok(postDto);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @DeleteMapping("/{postId}")
    @Operation(summary = "Delete a post by ID", description = "Deletes a post if the authenticated user is the author or an admin.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Post deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - user not authorized to delete post"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    public ResponseEntity<Void> deletePost(
            @Parameter(name = "postId", description = "ID of the post to delete", required = true) @PathVariable Long postId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        try {
            postService.deletePost(postId, userDetails.getUsername());
            return ResponseEntity.noContent().build();
        } catch (AccessDeniedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage(), e);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    // --- Like Management Endpoints ---

    @PostMapping("/{postId}/like")
    @Operation(summary = "Like a post", description = "Allows the authenticated user to like a specific post. If already liked, no change.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Post liked successfully or already liked"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Post not found")
    })
    public ResponseEntity<Void> likePost(
            @Parameter(name = "postId", description = "ID of the post to like", required = true) @PathVariable Long postId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        try {
            likeService.likePost(postId, userDetails.getUsername());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @DeleteMapping("/{postId}/like")
    @Operation(summary = "Unlike a post", description = "Allows the authenticated user to remove their like from a specific post.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Post unliked successfully or was not liked"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Post not found or not liked by user")
    })
    public ResponseEntity<Void> unlikePost(
            @Parameter(name = "postId", description = "ID of the post to unlike", required = true) @PathVariable Long postId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        try {
            likeService.unlikePost(postId, userDetails.getUsername());
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @GetMapping("/{postId}/like/status")
    @Operation(summary = "Check if user liked a post", description = "Checks if the authenticated user has liked a specific post.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully checked like status", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Post not found")
    })
    public ResponseEntity<Boolean> hasUserLikedPost(
            @Parameter(name = "postId", description = "ID of the post to check", required = true) @PathVariable Long postId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        try {
            boolean liked = likeService.hasUserLikedPost(postId, userDetails.getUsername());
            return ResponseEntity.ok(liked);
        } catch (RuntimeException e) {
             throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }
}
