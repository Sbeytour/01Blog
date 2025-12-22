package blog.dto.response;

import java.time.LocalDateTime;

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
    private Boolean isBanned;
    private long reportCount;
    private LocalDateTime joinedDate;

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
        userResp.joinedDate = user.getJoinedDate();
        return userResp;
    }

    public static UserResponseDto forAdminDash(User user, long reportCount) {
        UserResponseDto dto = new UserResponseDto();
        dto.id = user.getId();
        dto.username = user.getUsername();
        dto.email = user.getEmail();
        dto.firstName = user.getFirstName();
        dto.lastName = user.getLastName();
        dto.role = user.getRole();
        dto.isBanned = user.getIsBanned();
        dto.profileImgUrl = user.getProfileImgUrl();
        dto.reportCount = reportCount;
        dto.joinedDate = user.getJoinedDate();
        return dto;
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

    public long getReportCount() {
        return reportCount;
    }

    public void setReportCount(long reportCount) {
        this.reportCount = reportCount;
    }

    public Boolean getIsBanned() {
        return isBanned;
    }

    public void setIsBanned(Boolean isBanned) {
        this.isBanned = isBanned;
    }

    public LocalDateTime getJoinedDate() {
        return joinedDate;
    }

    public void setJoinedDate(LocalDateTime joinedDate) {
        this.joinedDate = joinedDate;
    }

}