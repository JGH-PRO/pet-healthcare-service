package com.petcare.petcareapp.domain;

import com.petcare.petcareapp.domain.Subscription; // This import was correctly added by awk
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users") // "user" is often a reserved keyword in SQL
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 20)
    private String role; // e.g., "USER", "ADMIN"

    @Column(name = "oauth_provider", length = 50)
    private String oauthProvider; // e.g., "GOOGLE", "KAKAO"

    @Column(name = "oauth_id")
    private String oauthId;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Pet> pets = new ArrayList<>();

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<PostLike> likes = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Subscription> subscriptions = new ArrayList<>(); // Added missing field declaration

    // Constructors
    public User() {
    }

    public User(String username, String password, String email, String role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getOauthProvider() { return oauthProvider; }
    public void setOauthProvider(String oauthProvider) { this.oauthProvider = oauthProvider; }
    public String getOauthId() { return oauthId; }
    public void setOauthId(String oauthId) { this.oauthId = oauthId; }

    public List<Pet> getPets() { return pets; }
    public void setPets(List<Pet> pets) { this.pets = pets; }
    public void addPet(Pet pet) { this.pets.add(pet); pet.setOwner(this); }
    public void removePet(Pet pet) { this.pets.remove(pet); pet.setOwner(null); }

    // Post methods
    public List<Post> getPosts() { return posts; }
    public void setPosts(List<Post> posts) { this.posts = posts; }
    public void addPost(Post post) { this.posts.add(post); post.setAuthor(this); }
    public void removePost(Post post) { this.posts.remove(post); post.setAuthor(null); }

    // Comment methods
    public List<Comment> getComments() { return comments; }
    public void setComments(List<Comment> comments) { this.comments = comments; }
    public void addComment(Comment comment) { this.comments.add(comment); comment.setAuthor(this); }
    public void removeComment(Comment comment) { this.comments.remove(comment); comment.setAuthor(null); }

    // Like methods
    public Set<PostLike> getLikes() { return likes; }
    public void setLikes(Set<PostLike> likes) { this.likes = likes; }
    public void addLike(PostLike like) { this.likes.add(like); like.setUser(this); }
    public void removeLike(PostLike like) { this.likes.remove(like); like.setUser(null); }

    // Subscription methods
    public List<Subscription> getSubscriptions() { return subscriptions; }
    public void setSubscriptions(List<Subscription> subscriptions) { this.subscriptions = subscriptions; }
    public void addSubscription(Subscription subscription) { this.subscriptions.add(subscription); subscription.setUser(this); }
    public void removeSubscription(Subscription subscription) { this.subscriptions.remove(subscription); subscription.setUser(null); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
               Objects.equals(username, user.username) &&
               Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, email);
    }

    @Override
    public String toString() {
        return "User{" +
               "id=" + id +
               ", username='" + username + '\'' +
               ", email='" + email + '\'' +
               ", role='" + role + '\'' +
               '}';
    }
}
