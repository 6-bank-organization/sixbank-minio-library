package com.sixbank.miniolibrary.config;

/**
 * Configuration holder for MinIO connection settings.
 * <p>
 * Example:
 * MinioConfig config = new MinioConfig("http://localhost:9000", "username", "password");
 */
public class MinioConfig {
    private final String url;
    private final String accessKey;
    private final String secretKey;

    public MinioConfig(String url, String accessKey, String secretKey) {
        this.url = url;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public String getUrl() {
        return url;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }
}
