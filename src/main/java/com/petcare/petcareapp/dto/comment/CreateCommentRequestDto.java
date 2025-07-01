package com.petcare.petcareapp.dto.comment;

// Add validation like @NotBlank later
public class CreateCommentRequestDto {
    private String content;

    // Getters and Setters
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
