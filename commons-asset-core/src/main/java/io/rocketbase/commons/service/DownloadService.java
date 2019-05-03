package io.rocketbase.commons.service;

import io.rocketbase.commons.dto.asset.AssetType;
import io.rocketbase.commons.exception.AssetErrorCodes;
import lombok.*;
import org.apache.http.Header;
import org.apache.http.HttpResponse;

import java.io.File;

public interface DownloadService {

    /**
     * download url to tempfile with filename and type<br>
     * may throw {@link DownloadError} runtime exception
     */
    TempDownload downloadUrl(String url);

    default String extractFilename(HttpResponse response) {
        Header firstHeader = response.getFirstHeader("Content-Disposition");
        if (firstHeader != null) {
            return firstHeader.getValue().replaceFirst("(?i)^.*filename=\"?([^\"]+)\"?.*$", "$1");
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
        private AssetType type;
    }
}
