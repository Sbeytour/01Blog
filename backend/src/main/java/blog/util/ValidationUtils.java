package blog.util;

import blog.entity.Post;
import blog.entity.User;
import blog.exceptions.ResourceNotFoundException;
import blog.exceptions.UnauthorizedException;
import blog.exceptions.UserNotFoundException;
import blog.repositories.PostRepository;
import blog.repositories.UserRepository;

public class ValidationUtils {

    /**
     * Validates that a user exists and returns the user entity
     * @param userId User ID to validate
     * @param userRepository User repository
     * @return User entity if found
     * @throws UserNotFoundException if user is not found
     */
    public static User validateUserExists(Long userId, UserRepository userRepository) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
    }

    /**
     * Validates that a post exists and returns the post entity
     * @param postId Post ID to validate
     * @param postRepository Post repository
     * @return Post entity if found
     * @throws ResourceNotFoundException if post is not found
     */
    public static Post validatePostExists(Long postId, PostRepository postRepository) {
        return postRepository.findById(postId)
            .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
    }

    /**
     * Validates that the current user is the owner of a resource
     * @param ownerId ID of the resource owner
     * @param currentUserId ID of the current user
     * @param resourceType Type of resource (e.g., "post", "comment")
     * @throws UnauthorizedException if user is not the owner
     */
    public static void validateOwnership(Long ownerId, Long currentUserId, String resourceType) {
        if (!ownerId.equals(currentUserId)) {
            throw new UnauthorizedException("You are not authorized to modify this " + resourceType);
        }
    }
}
