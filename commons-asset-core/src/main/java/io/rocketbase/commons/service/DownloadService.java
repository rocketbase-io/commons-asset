package io.rocketbase.commons.service;

import io.rocketbase.commons.dto.asset.AssetType;
import io.rocketbase.commons.exception.AssetErrorCodes;
import lombok.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

public class DownloadService {

    private final Tika tika = new Tika();

    private CloseableHttpClient httpClient = HttpClientBuilder.create()
            .setConnectionTimeToLive(30, TimeUnit.SECONDS)
            .build();

    public TempDownload downloadUrl(String url) {
        File tempFile = null;
        String filename = FilenameUtils.getName(url);
        AssetType type = null;

        try {
            HttpGet getRequest = new HttpGet(new URI(url));
            HttpResponse response = httpClient.execute(getRequest);

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String extractedFilename = extractFilename(response);
                if (extractedFilename != null) {
                    filename = extractedFilename;
                }

                type = AssetType.findByFileExtension(FilenameUtils.getExtension(filename));
                if (type == null) {
                    // store at first as jpeg later it will get checked by tika during store
                    type = AssetType.JPEG;
                }
                tempFile = File.createTempFile("asset", type.getFileExtension());
                FileOutputStream outputStream = new FileOutputStream(tempFile);
                entity.writeTo(outputStream);
                outputStream.close();
            } else {
                throw new DownloadError(AssetErrorCodes.NOT_DOWNLOADABLE);
            }
        } catch (IOException | URISyntaxException e) {
            throw new DownloadError(AssetErrorCodes.NOT_DOWNLOADABLE);
        }
        return new TempDownload(tempFile, filename, type);
    }

    private String extractFilename(HttpResponse response) {
        Header firstHeader = response.getFirstHeader("Content-Disposition");
        if (firstHeader != null) {
            return firstHeader.getValue().replaceFirst("(?i)^.*filename=\"?([^\"]+)\"?.*$", "$1");
        }
        return null;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class TempDownload {
        private File file;
        private String filename;
        private AssetType type;
    }

    @Getter
    @RequiredArgsConstructor
    public static class DownloadError extends RuntimeException {
        private final AssetErrorCodes errorCode;
    }
}
