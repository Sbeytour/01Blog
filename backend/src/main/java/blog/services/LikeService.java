package blog.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import blog.dto.response.LikeResponseDto;
import blog.entity.Like;
import blog.entity.Post;
import blog.entity.User;
import blog.repositories.LikeRepository;
import blog.repositories.PostRepository;

@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;

    public LikeService(LikeRepository likeRepository, PostRepository postRepository) {
        this.likeRepository = likeRepository;
        this.postRepository = postRepository;
    }

    @Transactional
    public LikeResponseDto toggleLike(Long postId, User currentUser) {
        // Check if post exists
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // Check if user already liked this post
        boolean alreadyLiked = likeRepository.existByUserIdAndPostId(currentUser.getId(), postId);

        if (alreadyLiked) {
            // Unlike: Find and delete the like
            Like like = likeRepository.findByUserIdAndPostId(currentUser.getId(), postId);
            if (like != null) {
                likeRepository.delete(like);
            }
        } else {
            // Like: Create new like
            Like newLike = new Like();
            newLike.setUser(currentUser);
            newLike.setPost(post);
            likeRepository.save(newLike);
        }

        // Get updated like count
        long likesCount = likeRepository.countByPostId(postId);
        boolean isLiked = !alreadyLiked;

        return new LikeResponseDto(isLiked, likesCount);
    }
}
