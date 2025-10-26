package blog.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import blog.dto.request.CreatePostRequestDto;
import blog.dto.response.PostResponseDto;
import blog.entity.Media;
import blog.entity.MediaType;
import blog.entity.Post;
import blog.entity.User;
import blog.exceptions.UserNotFoundException;
import blog.repositories.MediaRepository;
import blog.repositories.PostRepository;
import blog.repositories.UserRepository;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MediaRepository mediaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String uploadsDir = "uploads/posts";

    public PostResponseDto createPost(CreatePostRequestDto createDto, User creator) {
        Post post = new Post();
        post.setTitle(createDto.getTitle());
        post.setContent(createDto.getContent());
        post.setCreator(creator);

        post = postRepository.save(post);

        List<MultipartFile> files = createDto.getFiles();
        if (files != null && !files.isEmpty()) {
            post.setMediaList(saveMediaFiles(files, post));
        }

        return PostResponseDto.fromEntity(post);
    }

    public List<PostResponseDto> getAllPosts(Long currentUserId) {
        // Get current user
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get posts from followed users only
        List<Post> posts = postRepository.findPostsByFollowedUsers(currentUser);

        return posts.stream()
                .map(post -> PostResponseDto.fromEntity(post, currentUserId))
                .toList();
    }

    public List<PostResponseDto> getPostsByUser(Long userId, Long currentUserId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found");
        }

        List<Post> posts = postRepository.findAll().stream().filter(post -> post.getCreator().getId() == userId)
                .toList();

        return posts.stream().map(post -> PostResponseDto.fromEntity(post, currentUserId)).toList();
    }

    public PostResponseDto getSinglePost(Long postId, Long currentUserId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));

        return PostResponseDto.fromEntity(post, currentUserId);
    }

    public PostResponseDto updatePost(Long postId, CreatePostRequestDto updateRequest, Long currentUserId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));

        if (post.getCreator().getId() != currentUserId) {
            throw new RuntimeException("You are not authorized to update this post");
        }

        post.setTitle(updateRequest.getTitle());
        post.setContent(updateRequest.getContent());

        // Delete specific media (only what was removed)
        String deleteMediaIdsJson = updateRequest.getDeleteMediaIds();
        if (deleteMediaIdsJson != null && !deleteMediaIdsJson.isEmpty()) {
            try {
                // convert the Json data to a list ,using jackson(convert json text into a java
                // object)
                List<Long> deletedIds = objectMapper.readValue(deleteMediaIdsJson, new TypeReference<List<Long>>() {
                });

                deleteSpecificMedia(post, deletedIds);
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse JSon data");
            }
        }

        // add new apploads media

        List<MultipartFile> files = updateRequest.getFiles();
        if (files != null && !files.isEmpty()) {
            // Save new media
            // post.setMediaList(saveMediaFiles(files, post));
            List<Media> newMedia = saveMediaFiles(files, post);
            for (Media media : newMedia) {
                post.getMediaList().add(media);
            }
        }

        Post updatedPost = postRepository.save(post);
        return PostResponseDto.fromEntity(updatedPost, currentUserId);
    }

    private void deleteSpecificMedia(Post post, List<Long> deletedIds) {
        List<Media> mediaToDelete = post.getMediaList().stream().filter(media -> deletedIds.contains(media.getId()))
                .toList();

        for (Media media : mediaToDelete) {
            // Delete file from disk
            try {
                String fileName = media.getUrl().substring(media.getUrl().lastIndexOf('/') + 1);
                Path filePath = Paths.get(uploadsDir).resolve(fileName).normalize();
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                System.err.println("Could not delete media file: " + media.getUrl());
            }

            // Remove from post's media list
            post.getMediaList().removeAll(mediaToDelete);
        }

        // Delete from database
        mediaRepository.deleteAll(mediaToDelete);
    }

    public void deletePost(Long postId, Long currentUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        if (post.getCreator().getId() != (currentUserId)) {
            throw new RuntimeException("You are not authorized to delete this post");
        }

        deleteOldMedia(post);
        postRepository.delete(post);
    }

    private List<Media> saveMediaFiles(List<MultipartFile> files, Post post) {
        List<Media> mediaList = new ArrayList<>();

        File dir = new File(uploadsDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        for (MultipartFile file : files) {
            String orgFileName = file.getOriginalFilename();
            String extension = orgFileName != null && orgFileName.contains(".")
                    ? orgFileName.substring(orgFileName.lastIndexOf("."))
                    : "";
            String fileName = UUID.randomUUID() + extension;
            Path filePath = Paths.get(uploadsDir, fileName);

            try {
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException("Failed to save file: " + fileName, e);
            }

            String contentType = file.getContentType();
            MediaType mediaType = contentType != null && contentType.startsWith("image")
                    ? MediaType.IMAGE
                    : MediaType.VIDEO;

            Media media = new Media();
            media.setUrl("/files/posts/" + fileName);
            media.setType(mediaType);
            media.setPost(post);

            mediaList.add(media);
        }

        return mediaRepository.saveAll(mediaList);
    }

    private void deleteOldMedia(Post post) {
        if (post.getMediaList() != null && !post.getMediaList().isEmpty()) {
            for (Media media : post.getMediaList()) {
                try {
                    String fileName = media.getUrl().substring(media.getUrl().lastIndexOf('/') + 1);
                    Path filePath = Paths.get(uploadsDir).resolve(fileName).normalize();
                    Files.deleteIfExists(filePath);
                } catch (IOException e) {
                    System.err.println("Could not delete old media file: " + media.getUrl());
                }
            }
            mediaRepository.deleteAll(post.getMediaList());
            post.getMediaList().clear();
        }
    }
}
