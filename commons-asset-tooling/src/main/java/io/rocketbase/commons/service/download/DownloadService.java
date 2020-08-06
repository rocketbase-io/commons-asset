package io.rocketbase.commons.service.download;

import io.rocketbase.commons.dto.asset.AssetType;
import io.rocketbase.commons.exception.AssetErrorCodes;
import lombok.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpResponse;

import java.io.File;

public interface DownloadService {

    /**
     * download url to tempfile with filename and type<br>
     * may throw {@link DownloadError} runtime exception
     */
    TempDownload downloadUrl(String url);

    default String extractFilename(ClientHttpResponse response) {
        String contentDisposition = response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION);
        if (contentDisposition != null) {
            return contentDisposition.replaceFirst("(?i)^.*filename=\"?([^\"]+)\"?.*$", "$1");
        }
        return null;
    }

    @Getter
    @RequiredArgsConstructor
    class DownloadError extends RuntimeException {
        private final AssetErrorCodes errorCode;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    class TempDownload {
        private File file;
        private String filename;
        /**
         * detected type by filename extension
         */
        private AssetType type;
    }
}
