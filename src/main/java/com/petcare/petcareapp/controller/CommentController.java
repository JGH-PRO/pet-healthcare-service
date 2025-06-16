package com.petcare.petcareapp.controller;

import com.petcare.petcareapp.dto.comment.CommentDto;
import com.petcare.petcareapp.dto.comment.CreateCommentRequestDto;
import com.petcare.petcareapp.service.CommentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
@Tag(name = "Comment Management", description = "APIs for adding, viewing, and managing comments on posts")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping
    @Operation(summary = "Add a comment to a post", description = "Creates a new comment on a specific post, authored by the authenticated user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Comment created successfully",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommentDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input or Post not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<CommentDto> addComment(
            @Parameter(description = "ID of the post to comment on", required = true) @PathVariable Long postId,
            @Valid @RequestBody CreateCommentRequestDto requestDto,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        try {
            CommentDto createdComment = commentService.addComment(postId, requestDto, userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @GetMapping
    @Operation(summary = "List all comments for a post", description = "Retrieves all comments for a specific post, ordered by creation date.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved comments",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommentDto.class))), // Note: Should be List<CommentDto> but Swagger schema for list is often inferred or handled by wrapper
        @ApiResponse(responseCode = "404", description = "Post not found")
    })
    public ResponseEntity<List<CommentDto>> getCommentsByPostId(
            @Parameter(description = "ID of the post whose comments are to be retrieved", required = true) @PathVariable Long postId) {
        try {
            List<CommentDto> comments = commentService.getCommentsByPostId(postId);
            return ResponseEntity.ok(comments);
        } catch (RuntimeException e) { // e.g. PostNotFoundException from service
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "Delete a comment by ID", description = "Deletes a comment if the authenticated user is the author of the comment, the author of the post, or an admin.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Comment deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - user not authorized to delete this comment"),
        @ApiResponse(responseCode = "404", description = "Post or Comment not found")
    })
    public ResponseEntity<Void> deleteComment(
            @Parameter(description = "ID of the post", required = true) @PathVariable Long postId,
            @Parameter(description = "ID of the comment to delete", required = true) @PathVariable Long commentId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        try {
            commentService.deleteComment(postId, commentId, userDetails.getUsername());
            return ResponseEntity.noContent().build();
        } catch (AccessDeniedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage(), e);
        } catch (RuntimeException e) { // e.g., CommentNotFoundException or PostNotFoundException
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }
}
