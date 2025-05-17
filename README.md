# 📦 MinIO Object Storage Library

A reusable, production-ready Java library for interacting with [MinIO](https://min.io/) — a high-performance, S3-compatible object storage system.

Designed with a clean API and modular structure, this library supports uploading, downloading, listing, and deleting objects (documents, images, videos, etc.) from MinIO buckets.

---

## ✨ Features

- ✅ Upload objects of any type (e.g., images, PDFs, large videos)
- ✅ Download files as streams
- ✅ Delete stored objects
- ✅ Auto-create buckets if they don't exist
- ✅ Clean, well-documented codebase

---

## 📦 Installation

### Maven
```xml
<dependency>
  <groupId>com.sixbank</groupId>
  <artifactId>minio-object-library</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</dependency>
````

### Gradle (Kotlin DSL)

```kotlin
dependencies {
    implementation("com.sixbank:minio-object-library:1.0.0")
}
```

---

## ⚙️ Configuration

### `MinioConfig.java`

```java
package com.sixbank.minio;

/**
 * Encapsulates MinIO connection properties.
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

    public String getUrl() { return url; }
    public String getAccessKey() { return accessKey; }
    public String getSecretKey() { return secretKey; }
}
```

---

### `MinioStorageClient.java`

```java
package com.sixbank.minio;

import io.minio.*;
import java.io.InputStream;

/**
 * Singleton client for MinIO object operations.
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

    public static synchronized MinioStorageClient getInstance(MinioConfig config) {
        if (instance == null) {
            instance = new MinioStorageClient(config);
        }
        return instance;
    }

    public String upload(InputStream inputStream, String contentType, String bucketName, String objectName) throws Exception {
        ensureBucketExists(bucketName);
        client.putObject(PutObjectArgs.builder()
            .bucket(bucketName)
            .object(objectName)
            .stream(inputStream, -1, 10485760) // 10MB part size
            .contentType(contentType)
            .build());
        return objectName;
    }

    public InputStream download(String bucketName, String objectName) throws Exception {
        return client.getObject(GetObjectArgs.builder()
            .bucket(bucketName)
            .object(objectName)
            .build());
    }

    public void delete(String bucketName, String objectName) throws Exception {
        client.removeObject(RemoveObjectArgs.builder()
            .bucket(bucketName)
            .object(objectName)
            .build());
    }

    private void ensureBucketExists(String bucketName) throws Exception {
        boolean exists = client.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!exists) {
            client.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }
}
```

---

## 🚀 Usage

```java
MinioConfig config = new MinioConfig("http://localhost:9000", "minioadmin", "minioadmin");
MinioStorageClient client = MinioStorageClient.getInstance(config);

// Upload
try (InputStream fileStream = new FileInputStream("lecture1.mp4")) {
    client.upload(fileStream, "video/mp4", "videos", "lecture1.mp4");
}

// Download
try (InputStream inputStream = client.download("videos", "lecture1.mp4")) {
    // Use the stream
}

// Delete
client.delete("videos", "lecture1.mp4");
```

---

## 🧪 Testing

Unit tests are available under `src/test`. For integration testing, consider using [Testcontainers](https://www.testcontainers.org/) to spin up a MinIO instance.

---

## 🧱 Design Pattern

**Factory Pattern** – Used for controlled instantiation of `MinioStorageClient`.

---

## 🗂️ Project Structure

```
sixbank-minio-library/
├── src/
│   ├── main/
│   │   ├── java/com/sixbank/minio/
|   |   |   ├── client
│   │   │       ├── MinioStorageClient.java
|   |   |   ├── config
│   │   │       └── MinioConfig.java
│   │   └── resources/
│   │       └── application.yml (optional)
├── build.gradle.kts / pom.xml
└── README.md
```

---

## 📄 License

MIT License

---

## 👥 Contributors

* Engineering Team @ SixBank

---

Have questions or want to improve the library? Feel free to open an issue or submit a PR!
