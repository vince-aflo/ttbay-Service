package io.turntabl.ttbay.controller;

import io.turntabl.ttbay.service.Impl.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URL;

@RestController
@RequestMapping("/api/v1")
public class S3Controller{
    private final S3Service s3Service;

    @Autowired
    public S3Controller(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @GetMapping("/upload-url")
    public ResponseEntity<String> getPresignedUrl(@RequestParam String objectKey, @RequestParam String contentType){
        System.out.println("s3 controller called");
        URL url = s3Service.generatePresignedUrl(objectKey, contentType);
//        UrlResponse urlResponse = new UrlResponse(url.toString());
        return ResponseEntity.ok(url.toString());
    }
}
