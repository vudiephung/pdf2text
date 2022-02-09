/* Notes:
 * For this coding project, the application could be operated by using the local memory only,
 * however, I still make use of Amazon S3 to store the converted text file for more advanced use cases.
 * E.g: User could download their file multiple times with the provided url (via "/file/download/{filename}")
 * within the time limit (i.e., the system would automatically delete the file after 12 hours after they were uploaded)
 *  */

package com.challenge.pdftotext.controller;

import com.challenge.pdftotext.service.StorageService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Controller
@ControllerAdvice
@RequestMapping("/file")
public class StorageController {

    @Autowired
    private StorageService service;

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;

    @GetMapping("/download/{fileName}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String fileName) throws ExecutionException,
            InterruptedException {
        byte[] data = service.downloadFile(fileName).get();
        ByteArrayResource resource = new ByteArrayResource(data);
        return ResponseEntity.ok().contentLength(data.length).
                header("Content-type", "application/octet-stream").
                header("Content-disposition", "attachment; filename=\"" + fileName + "\"").
                body(resource);
    }

    @PostMapping("/upload")
    public ResponseEntity<ByteArrayResource> convertAndDownload(@RequestParam(value = "file") MultipartFile file)
            throws IOException, ExecutionException, InterruptedException {
        String fileName = service.convertAndUploadFile(file).get();
        return downloadFile(fileName);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleExceedMaxSize(@NotNull RedirectAttributes ra) {
        ra.addFlashAttribute("limitSizeError", "File size must not exceed " + maxFileSize);
        return "redirect:/";
    }

}
