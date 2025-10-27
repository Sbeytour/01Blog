package blog.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import blog.entity.Role;
import blog.entity.User;

public class UserResponseDto {

    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String bio;
    private String profileImgUrl;
    private Role role;
    private Long followersCount;
    private Long followingCount;
    private Boolean isFollowedByCurrentUser;

    public UserResponseDto() {
    }

    public static UserResponseDto fromEntity(User user) {
        UserResponseDto userResp = new UserResponseDto();
        userResp.id = user.getId();
        userResp.username = user.getUsername();
        userResp.email = user.getEmail();
        userResp.firstName = user.getFirstName();
        userResp.lastName = user.getLastName();
        userResp.bio = user.getBio();
        userResp.profileImgUrl = user.getProfileImgUrl();
        userResp.role = user.getRole();
        return userResp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getProfileImgUrl() {
        return profileImgUrl;
    }

    public void setProfileImgUrl(String profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Long getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(Long followersCount) {
        this.followersCount = followersCount;
    }

    public Long getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(Long followingCount) {
        this.followingCount = followingCount;
    }

    public Boolean getIsFollowedByCurrentUser() {
        return isFollowedByCurrentUser;
    }

    public void setIsFollowedByCurrentUser(Boolean isFollowedByCurrentUser) {
        this.isFollowedByCurrentUser = isFollowedByCurrentUser;
    }
}