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

import blog.dto.request.CreatePostRequestDto;
import blog.dto.response.PostResponseDto;
import blog.entity.Media;
import blog.entity.MediaType;
import blog.entity.Post;
import blog.entity.User;
import blog.repositories.MediaRepository;
import blog.repositories.PostRepository;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MediaRepository mediaRepository;

    private String uploadsDir = "uploads/posts";

    public PostResponseDto createPost(CreatePostRequestDto createRequest, User creator, List<MultipartFile> files) {
        Post post = new Post();
        post.setTitle(createRequest.getTitle());
        post.setContent(createRequest.getContent());
        post.setCreator(creator);

        post = postRepository.save(post);

        if (files != null && !files.isEmpty()) {
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
                MediaType mediaType = contentType != null && contentType.startsWith("image") ? MediaType.IMAGE
                        : MediaType.VIDEO;

                Media media = new Media();
                media.setUrl("/files/posts/" + fileName);
                media.setType(mediaType);
                media.setPost(post);

                mediaList.add(media);
            }

            mediaRepository.saveAll(mediaList);
            post.setMediaList(mediaList);
        }

        return PostResponseDto.fromEntity(post);
    }
}
