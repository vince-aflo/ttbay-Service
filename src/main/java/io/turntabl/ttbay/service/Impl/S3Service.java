package io.turntabl.ttbay.service.Impl;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Date;

@Service
public class S3Service{
    private final AmazonS3 s3Client;

    public S3Service(){
        String accessKey = System.getenv("AWS_ACCESS_KEY");
        String secretKey = System.getenv("AWS_SECRET_KEY");
        Regions region = Regions.US_EAST_1;
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }

    public URL generatePresignedUrl(String objectKey, String contentType){
        Date expiration = new Date(System.currentTimeMillis() + 360000); // URL expires after 1 hour = 360000
        String bucketName = "ttbay-demo";
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucketName, objectKey)
                        .withMethod(HttpMethod.valueOf("PUT"))
                        .withExpiration(expiration)
                        .withContentType(contentType);
        return s3Client.generatePresignedUrl(generatePresignedUrlRequest);
    }
}
