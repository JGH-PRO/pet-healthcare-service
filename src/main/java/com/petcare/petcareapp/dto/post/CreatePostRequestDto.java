package com.petcare.petcareapp.dto.post;

// Add validation annotations later if desired e.g. @NotBlank
public class CreatePostRequestDto {
    private String content;
    private String imageUrl; // Optional

    // Getters and Setters
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
