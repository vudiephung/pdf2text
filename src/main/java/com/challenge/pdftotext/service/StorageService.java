package com.challenge.pdftotext.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static jdk.nashorn.internal.objects.NativeMath.log;


@Service
@Slf4j
public class StorageService {

    @Value("${application.bucket.name}")
    private String bucketName;

    @Autowired
    private AmazonS3 s3Client;

    @Async
    public CompletableFuture<String> convertAndUploadFile(MultipartFile file) throws IOException, ExecutionException,
            InterruptedException {
        File fileObj = convertMultiPartFileToFile(file).get();

        // Convert to text
        File convertedFile = ConverterService.convertPDFToText(fileObj);

        String fileName = System.currentTimeMillis() + "_" + convertedFile.getName();
        s3Client.putObject(new PutObjectRequest(bucketName, fileName, convertedFile));

        // Delete the generated local files
        fileObj.delete();
        convertedFile.delete();

        return CompletableFuture.completedFuture(fileName);
    }

    @Async
    public CompletableFuture<byte[]> downloadFile(String fileName) {
        S3Object s3Object = s3Client.getObject(bucketName, fileName);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        try {
            byte[] content = IOUtils.toByteArray(inputStream);
            return CompletableFuture.completedFuture(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

//    public String deleteFile(String fileName) {
//        s3Client.deleteObject(bucketName, fileName);
//        return fileName + " removed ...";
//    }

    @Async
    private CompletableFuture<File> convertMultiPartFileToFile(MultipartFile file) {
        File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            log("Error converting multipartFile to file", e);
        }
        return CompletableFuture.completedFuture(convertedFile);
    }
}
