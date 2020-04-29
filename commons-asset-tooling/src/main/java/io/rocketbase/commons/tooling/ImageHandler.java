package io.rocketbase.commons.tooling;

import com.google.common.io.BaseEncoding;
import io.rocketbase.commons.dto.asset.AssetType;
import io.rocketbase.commons.service.DefaultDownloadService;
import io.rocketbase.commons.service.DownloadService;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.IOUtils;
import org.apache.tika.Tika;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

/**
 * Helper for downloading an image and get as result the detected type, binary + optional base64 string
 */
public final class ImageHandler {

    public static final DefaultDownloadService DEFAULT_DOWNLOAD_SERVICE = new DefaultDownloadService();
    private static final Tika TIKA = new Tika();

    /**
     * begin download url and instruct by fluent methods
     *
     * @param url source of image
     * @return fluent builder
     */
    public static ImageHandlingBuilder download(String url) {
        return new ImageHandlingBuilder(url);
    }

    @SneakyThrows
    static ImageHandlingResult build(ImageHandlingBuilder config) {
        DownloadService.TempDownload tempDownload = config.downloadService.downloadUrl(config.url);
        if (config.enforceTika || tempDownload.getType() == null) {
            String contentType = TIKA.detect(tempDownload.getFile());
            tempDownload.setType(AssetType.findByContentType(contentType));
        }


        if (tempDownload.getType() == null || !tempDownload.getType().isJavaProcessableImage()) {
            throw new RuntimeException("Source is not an image!");
        }

        if (config.width != null || config.height != null) {
            Thumbnails.Builder<File> thumbBuilder = Thumbnails.of(tempDownload.getFile());
            if (config.width == null || config.height == null) {
                if (config.width != null) {
                    thumbBuilder.width(config.width);
                } else {
                    thumbBuilder.height(config.height);
                }
            } else {
                thumbBuilder.scale(config.width, config.height);
            }
            @Cleanup ByteArrayOutputStream thumbOs = new ByteArrayOutputStream();
            thumbBuilder.toOutputStream(thumbOs);

            // delete temp file
            tempDownload.getFile().delete();

            return new ImageHandlingResult(thumbOs.toByteArray(), tempDownload.getType());
        } else {
            return new ImageHandlingResult(IOUtils.toByteArray(new FileInputStream(tempDownload.getFile())), tempDownload.getType());
        }
    }

    @RequiredArgsConstructor
    public static class ImageHandlingResult {
        private final byte[] binary;
        private final AssetType assetType;

        public String base64() {
            return "data:" + assetType.getContentType() + ";base64," + BaseEncoding.base64().encode(binary);
        }

        public byte[] binary() {
            return binary;
        }

        public AssetType assetType() {
            return assetType;
        }

    }

    @RequiredArgsConstructor
    public static class ImageHandlingBuilder {
        private final String url;
        private Integer width;
        private Integer height;
        private boolean enforceTika;
        private DownloadService downloadService = DEFAULT_DOWNLOAD_SERVICE;

        /**
         * allows to resize source - when height not it will get auto calculated<br>
         * when both width + height are set image will get sized to fit in box-size<br>
         * always the aspect ratio of the original image will be preserved
         */
        public ImageHandlingBuilder width(int width) {
            this.width = width;
            return this;
        }

        /**
         * allows to resize source - when width not it will get auto calculated<br>
         * when both width + height are set image will get sized to fit in box-size<br>
         * always the aspect ratio of the original image will be preserved
         */
        public ImageHandlingBuilder height(int height) {
            this.height = height;
            return this;
        }

        /**
         * by default for better speed image-type detection will get done if possible by filename extension<br>
         * tika will only get used in case no type could be detected via extensions <br>
         * you can enforce this detection...
         */
        public ImageHandlingBuilder enforceTika() {
            this.enforceTika = true;
            return this;
        }

        /**
         * in case you would like to use a custom download service instead of {@link DefaultDownloadService}
         */
        public ImageHandlingBuilder customDownloadService(DownloadService downloadService) {
            this.downloadService = downloadService;
            return this;
        }

        /**
         * download source and returns different options
         */
        public ImageHandlingResult process() {
            return ImageHandler.build(this);
        }
    }
}
