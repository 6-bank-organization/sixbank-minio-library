package com.sixbank.miniolibrary.client;

import com.sixbank.miniolibrary.config.MinioConfig;
import io.minio.*;

import java.io.InputStream;

/**
 * Singleton client for interacting with MinIO object storage.
 * <p>
 * Responsibilities include uploading, downloading, and deleting objects,
 * with automatic bucket creation if not already existing.
 *
 * <p>Example usage:</p>
 * <pre>
 *     MinioConfig config = new MinioConfig("http://localhost:9000", "minioadmin", "minioadmin");
 *     MinioStorageClient client = MinioStorageClient.getInstance(config);
 *
 *     // Upload
 *     try (InputStream stream = new FileInputStream("example.mp4")) {
 *         client.upload(stream, "video/mp4", "videos", "example.mp4");
 *     }
 *
 *     // Download
 *     InputStream downloaded = client.download("videos", "example.mp4");
 *
 *     // Delete
 *     client.delete("videos", "example.mp4");
 * </pre>
 */
public class MinioStorageClient {
    private static MinioStorageClient instance;
    private final MinioClient client;

    private MinioStorageClient(MinioConfig config) {
        this.client = MinioClient.builder()
                .endpoint(config.getUrl())
                .credentials(config.getAccessKey(), config.getSecretKey())
                .build();
    }

    /**
     * Returns a singleton instance configured with given settings.
     */
    public static synchronized MinioStorageClient getInstance(MinioConfig config) {
        if (instance == null) {
            instance = new MinioStorageClient(config);
        }
        return instance;
    }

    /**
     * Uploads an object to a specified bucket.
     *
     * @param inputStream the file stream
     * @param contentType MIME type of the file
     * @param bucketName  target bucket
     * @param objectName  object key
     * @return the object name
     * @throws Exception if upload fails
     */
    public String upload(InputStream inputStream, String contentType, String bucketName, String objectName) throws Exception {
        ensureBucketExists(bucketName);

        PutObjectArgs args = PutObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .stream(inputStream, -1, 10485760)
                .contentType(contentType)
                .build();

        client.putObject(args);
        return objectName;
    }

    /**
     * Downloads an object from a bucket.
     *
     * @param bucketName bucket name
     * @param objectName object key
     * @return object as InputStream
     * @throws Exception if object not found
     */
    public InputStream download(String bucketName, String objectName) throws Exception {
        GetObjectArgs args = GetObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .build();

        return client.getObject(args);
    }

    /**
     * Deletes an object from a bucket.
     *
     * @param bucketName bucket name
     * @param objectName object key
     * @throws Exception if deletion fails
     */
    public void delete(String bucketName, String objectName) throws Exception {
        RemoveObjectArgs args = RemoveObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .build();

        client.removeObject(args);
    }

    /**
     * Ensures a bucket exists before performing operations.
     * If it doesn't exist, it will be created.
     */
    private void ensureBucketExists(String bucketName) throws Exception {
        boolean found = client.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!found) {
            client.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }
}
