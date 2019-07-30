package io.rocketbase.commons.tooling;

import com.google.common.io.BaseEncoding;
import io.rocketbase.commons.dto.asset.AssetType;
import io.rocketbase.commons.service.DefaultDownloadService;
import io.rocketbase.commons.service.DownloadService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.tika.Tika;

import java.io.ByteArrayOutputStream;
import java.io.File;

public final class ImageHandling {

    public static final DefaultDownloadService DEFAULT_DOWNLOAD_SERVICE = new DefaultDownloadService();
    private static final Tika TIKA = new Tika();

    public static ImageHandlingBuilder builder(String url) {
        return new ImageHandlingBuilder(url);
    }

    @SneakyThrows
    static ImageHandlingResult build(DownloadService downloadService, String url, Integer width, Integer height) {
        DownloadService.TempDownload tempDownload = downloadService.downloadUrl(url);
        String contentType = TIKA.detect(tempDownload.getFile());
        AssetType assetType = AssetType.findByContentType(contentType);

        if (!assetType.isImage()) {
            throw new RuntimeException("Source is not an image!");
        }

        Thumbnails.Builder<File> thumbBuilder = Thumbnails.of(tempDownload.getFile());
        if (width == null || height == null) {
            if (width != null) {
                thumbBuilder.width(width);
            } else {
                thumbBuilder.height(height);
            }
        } else {
            thumbBuilder.scale(width, height);
        }
        ByteArrayOutputStream thumbOs = new ByteArrayOutputStream();
        thumbBuilder.toOutputStream(thumbOs);

        // delete temp file
        tempDownload.getFile().delete();

        return new ImageHandlingResult(thumbOs, assetType);
    }

    @RequiredArgsConstructor
    public static class ImageHandlingResult {
        private final ByteArrayOutputStream outputStream;
        private final AssetType assetType;

        public String base64() {
            return "data:" + assetType.getContentType() + ";base64," + BaseEncoding.base64().encode(outputStream.toByteArray());
        }

        public ByteArrayOutputStream stream() {
            return outputStream;
        }

        public AssetType assetType() {
            return assetType;
        }

    }

    @RequiredArgsConstructor
    private static class ImageHandlingBuilder {
        private final String url;
        private Integer width;
        private Integer height;
        private DownloadService downloadService = DEFAULT_DOWNLOAD_SERVICE;

        public ImageHandlingBuilder width(int width) {
            this.width = width;
            return this;
        }

        public ImageHandlingBuilder height(int height) {
            this.height = height;
            return this;
        }

        /**
         * in case you would like to use a custom download service instead of {@link DefaultDownloadService}
         */
        public ImageHandlingBuilder customDownloadService(DownloadService downloadService) {
            this.downloadService = downloadService;
            return this;
        }

        public ImageHandlingResult build() {
            return ImageHandling.build(downloadService, url, width, height);
        }
    }
}
