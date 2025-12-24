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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import blog.Config.FileStorageConfig;
import blog.dto.request.CreatePostRequestDto;
import blog.dto.response.PageResponse;
import blog.dto.response.PostResponseDto;
import blog.entity.Media;
import blog.entity.MediaType;
import blog.entity.Post;
import blog.entity.User;
import blog.exceptions.UserNotFoundException;
import blog.repositories.MediaRepository;
import blog.repositories.PostRepository;
import blog.repositories.UserRepository;
import blog.util.FileStorageUtils;
import blog.util.ValidationUtils;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final MediaRepository mediaRepository;
    private final ObjectMapper objectMapper;
    private final FileStorageConfig fileStorageConfig;
    private final NotificationService notificationService;

    public PostService(PostRepository postRepository, UserRepository userRepository, MediaRepository mediaRepository,
            ObjectMapper objectMapper, FileStorageConfig fileStorageConfig, NotificationService notificationService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.mediaRepository = mediaRepository;
        this.objectMapper = objectMapper;
        this.fileStorageConfig = fileStorageConfig;
        this.notificationService = notificationService;
    }

    @Transactional
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

        // Notify followers about the new post
        notificationService.notifyFollowersAboutNewPost(post);

        return PostResponseDto.fromEntity(post);
    }

    public PageResponse<PostResponseDto> getAllPosts(Long currentUserId, int page, int size) {
        // Get current user
        User currentUser = ValidationUtils.validateUserExists(currentUserId, userRepository);

        // Create pageable with sorting by creation date descending
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        // Get posts from followed users only with pagination
        Page<Post> postPage = postRepository.findPostsByFollowedUsersPaged(currentUser, pageable);

        // Convert to DTOs
        List<PostResponseDto> postDtos = postPage.getContent().stream()
                .map(post -> PostResponseDto.fromEntity(post, currentUserId))
                .toList();

        return new PageResponse<>(
                postDtos,
                postPage.getNumber(),
                postPage.getTotalPages(),
                postPage.getTotalElements());
    }

    public PageResponse<PostResponseDto> getPostsByUser(Long userId, Long currentUserId, int page, int size) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found");
        }
        Pageable pageable = PageRequest.of(page, size);

        Page<Post> posts = postRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);

        List<PostResponseDto> postDtos = posts.getContent().stream()
                .map(post -> PostResponseDto.fromEntity(post, currentUserId))
                .toList();

        return new PageResponse<>(
                postDtos,
                posts.getNumber(),
                posts.getTotalPages(),
                posts.getTotalElements());
    }

    public PostResponseDto getSinglePost(Long postId, Long currentUserId) {
        Post post = ValidationUtils.validatePostExists(postId, postRepository);

        return PostResponseDto.fromEntity(post, currentUserId);
    }

    public PostResponseDto updatePost(Long postId, CreatePostRequestDto updateRequest, Long currentUserId) {
        Post post = ValidationUtils.validatePostExists(postId, postRepository);
        ValidationUtils.validateOwnership(post.getCreator().getId(), currentUserId, "post");

        post.setTitle(updateRequest.getTitle());
        post.setContent(updateRequest.getContent());

        // Delete specific media (only what was removed)
        String deleteMediaIdsJson = updateRequest.getDeletedMediaIds();
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
            String uploadsDir = fileStorageConfig.getUploadDir() + "/posts";
            FileStorageUtils.deleteFile(media.getUrl(), uploadsDir);
        }
        // Remove from post's media list
        post.getMediaList().removeAll(mediaToDelete);

        // Delete from database
        mediaRepository.deleteAll(mediaToDelete);
    }

    public void deletePost(Long postId, Long currentUserId) {
        Post post = ValidationUtils.validatePostExists(postId, postRepository);
        ValidationUtils.validateOwnership(post.getCreator().getId(), currentUserId, "post");

        deleteOldMedia(post);
        postRepository.delete(post);
    }

    private List<Media> saveMediaFiles(List<MultipartFile> files, Post post) {
        List<Media> mediaList = new ArrayList<>();

        String uploadsDir = fileStorageConfig.getUploadDir() + "/posts";
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
            String uploadsDir = fileStorageConfig.getUploadDir() + "/posts";
            for (Media media : post.getMediaList()) {
                FileStorageUtils.deleteFile(media.getUrl(), uploadsDir);
            }
            mediaRepository.deleteAll(post.getMediaList());
            post.getMediaList().clear();
        }
    }
}
