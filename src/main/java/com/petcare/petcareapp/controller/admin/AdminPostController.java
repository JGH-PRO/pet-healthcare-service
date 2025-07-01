package com.petcare.petcareapp.controller.admin;

import com.petcare.petcareapp.dto.post.PostDto; // Reusing
import com.petcare.petcareapp.service.admin.AdminPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admin/posts")
@Tag(name = "Admin: Post Management", description = "APIs for administrators to manage all community posts.")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPostController {

    @Autowired
    private AdminPostService adminPostService;

    @GetMapping
    @Operation(summary = "List all posts (Admin)", description = "Retrieves a paginated list of all posts in the system, ordered by creation date (newest first).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of posts", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))), // Page of PostDto
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<Page<PostDto>> getAllPosts(
            @Parameter(description = "Pagination and sorting parameters") @PageableDefault(size = 20, sort = "createdAt,desc") Pageable pageable) {
        Page<PostDto> posts = adminPostService.getAllPosts(pageable);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{postId}")
    @Operation(summary = "Get post by ID (Admin)", description = "Retrieves details for a specific post by its ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved post details", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PostDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Post not found")
    })
    public ResponseEntity<PostDto> getPostById(
            @Parameter(name = "postId", description = "ID of the post to retrieve", required = true) @PathVariable Long postId) {
        try {
            PostDto postDto = adminPostService.getPostById(postId);
            return ResponseEntity.ok(postDto);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @DeleteMapping("/{postId}")
    @Operation(summary = "Delete post by ID (Admin)", description = "Deletes any post from the system by its ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Post deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Post not found")
    })
    public ResponseEntity<Void> deletePost(
            @Parameter(name = "postId", description = "ID of the post to delete", required = true) @PathVariable Long postId) {
        try {
            adminPostService.deletePost(postId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }
}
