package blog.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterRequestDto {
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]{4,12}$", message = "username must be between 4 and 12 characters and contain only characters and numbers")
    private String username;

    @Size(min = 3, max = 15)
    private String firstName;

    @Size(min = 3, max = 15)
    private String lastName;

    @Email
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]+\\.?[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.[a-z]+$", message = "Email format is invalid")
    private String email;

    @NotBlank
    @Size(min = 3, max = 30)
    private String password;

    @Size(min = 3, max = 1000)
    private String bio;

    @Size(max = 500)
    private String profileImgUrl;

    public String getProfileImgUrl() {
        return profileImgUrl;
    }

    public void setProfileImgUrl(String profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
