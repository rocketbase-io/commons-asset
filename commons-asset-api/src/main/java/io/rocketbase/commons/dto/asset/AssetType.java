package io.rocketbase.commons.dto.asset;

import io.rocketbase.commons.util.Nulls;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public enum AssetType {

    JPEG("image/jpeg", "jpg"),
    PNG("image/png"),
    APNG("image/apng"),
    GIF("image/gif"),
    TIFF("image/tiff"),
    BMP("image/bmp"),
    ICO("image/x-ico", "ico"),
    SVG("image/svg+xml", "svg"),
    WEBP("image/webp"),
    HEIF("image/heif"),
    HEIC("image/heic"),
    PDF("application/pdf"),
    ZIP("application/zip"),
    CSV("application/csv"),
    XLS("application/msexcel"),
    XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
    DOC("application/msword"),
    DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    PPT("application/vnd.ms-powerpoint"),
    PPTX("application/vnd.openxmlformats-officedocument.presentationml.presentation");

    @Getter
    private String contentType;
    private String fileExtension;

    AssetType(String contentType) {
        this(contentType, null);
    }

    AssetType(String contentType, String fileExtension) {
        this.contentType = contentType;
        this.fileExtension = fileExtension;
    }

    public static Set<String> getSupportedContentTypes() {
        Set<String> result = new HashSet<>();
        for (AssetType type : values()) {
            result.add(type.getContentType());
        }
        return result;
    }

    public static List<AssetType> getAllType() {
        return Arrays.asList(values());
    }

    public static AssetType findByContentType(String contentType) {
        if (contentType != null) {
            for (AssetType type : values()) {
                if (contentType.equalsIgnoreCase(type.getContentType())) {
                    return type;
                }
            }
        }
        return null;
    }

    public static AssetType findByFileExtension(String fileExtensions) {
        if (fileExtensions != null) {
            for (AssetType type : values()) {
                if (fileExtensions.equalsIgnoreCase(type.getFileExtension())) {
                    return type;
                } else if (type.equals(JPEG) && fileExtensions.equalsIgnoreCase("jpeg")) {
                    return type;
                } else if (type.equals(TIFF) && fileExtensions.equalsIgnoreCase("tif")) {
                    return type;
                }
            }
        }
        return null;
    }

    public boolean isImage() {
        return getContentType().contains("image");
    }

    public boolean couldHaveTransparency() {
        return Arrays.asList(PNG, GIF).contains(this);
    }

    public String getFileExtension() {
        return Nulls.notNull(fileExtension, name().toLowerCase());
    }

    public String getFileExtensionForSuffix() {
        return "." + getFileExtension();
    }
}
