// package blog.controller;

// import java.nio.file.Files;
// import java.nio.file.Path;
// import java.nio.file.Paths;

// import org.springframework.http.MediaType;
// import org.springframework.core.io.Resource;
// import org.springframework.core.io.UrlResource;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// @RestController
// @RequestMapping("/files")
// public class FileController {
//     private String uploadDir = "uploads";

//     @GetMapping("/{filename:.+}")
//     public ResponseEntity<Resource> getFile(@PathVariable String filename) {
//         try {
//             Path filePath = Paths.get(uploadDir).resolve(filename).normalize();
//             System.out.println(filePath);
//             Resource resource = new UrlResource(filePath.toUri());

//             if (!resource.exists()) {
//                return ResponseEntity.notFound().build();
//             }

//             String contentType = Files.probeContentType(filePath);
//             if (contentType == null) {
//                 contentType = "application/octet-stream";
//             }

//             return ResponseEntity.ok()
//                     .contentType(MediaType.parseMediaType(contentType))
//                     .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
//                     .body(resource);
//         } catch (Exception e) {
//             throw new RuntimeException("Error loading file: " + filename, e);
//         }

//     }
// }
