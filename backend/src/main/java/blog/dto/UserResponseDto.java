package blog.dto;

import blog.entity.User;

public class UserResponseDto {

    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String bio;

    public UserResponseDto() {
    }

    public static UserResponseDto fromEntity(User user) {
        UserResponseDto userResp = new UserResponseDto();
        userResp.username = user.getUsername();
        userResp.email = user.getEmail();
        userResp.firstName = user.getFirstName();
        userResp.lastName = user.getLastName();
        userResp.bio = user.getBio();
        return userResp;
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
    
}