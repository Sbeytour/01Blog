package blog.util;

import blog.entity.Post;
import blog.entity.User;
import blog.exceptions.ResourceNotFoundException;
import blog.exceptions.UnauthorizedException;
import blog.exceptions.UserNotFoundException;
import blog.repositories.PostRepository;
import blog.repositories.UserRepository;

public class ValidationUtils {

    public static User validateUserExists(Long userId, UserRepository userRepository) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
    }

    public static Post validatePostExists(Long postId, PostRepository postRepository) {
        return postRepository.findById(postId)
            .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
    }

    public static void validateOwnership(Long ownerId, Long currentUserId, String resourceType) {
        if (!ownerId.equals(currentUserId)) {
            throw new UnauthorizedException("You are not authorized to modify this " + resourceType);
        }
    }
}
