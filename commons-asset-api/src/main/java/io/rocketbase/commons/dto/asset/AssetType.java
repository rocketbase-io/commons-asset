package io.rocketbase.commons.dto.asset;

import com.fasterxml.jackson.annotation.JsonValue;
import io.rocketbase.commons.util.Nulls;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public enum AssetType {

    // images
    JPEG("jpeg", "image/jpeg", "jpg"),
    PNG("png", "image/png"),
    APNG("apng", "image/apng"),
    GIF("gif", "image/gif"),
    TIFF("tiff", "image/tiff"),
    BMP("bmp", "image/bmp"),
    ICO("ico", "image/x-ico", "ico"),
    SVG("svg", "image/svg+xml", "svg"),
    WEBP("webp", "image/webp"),
    HEIF("heif", "image/heif"),
    HEIC("heic", "image/heic"),
    // pdf
    PDF("pdf", "application/pdf"),
    // compressed files
    ZIP("zip", "application/zip"),
    TAR("tar", "application/x-tar"),
    RAR("rar", "application/vnd.rar"),
    GZIP("gzip", "application/gzip"),
    _7z("7z", "application/x-7z-compressed"),
    // microsoft office
    XLS("xls", "application/msexcel"),
    XLSX("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
    DOC("doc", "application/msword"),
    DOCX("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    PPT("ppt", "application/vnd.ms-powerpoint"),
    PPTX("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"),
    // open document
    ODP("odp", "application/vnd.oasis.opendocument.presentation"),
    ODS("ods", "application/vnd.oasis.opendocument.spreadsheet"),
    ODT("odt", "application/vnd.oasis.opendocument.text"),
    // text files
    CSV("csv", "application/csv"),
    TXT("txt", "application/text"),
    JSON("json", "application/json"),
    RTF("rtf", "application/rtf"),
    XML("xml", "application/xml"),
    // video
    MPEG("mpeg", "video/mpeg"),
    MP4("mp4", "video/mp4"),
    MPV("mpv", "video/mpv"),
    MOV("mov", "video/quicktime"),
    AVI("avi", "video/x-msvideo"),
    WMV("wmv", "video/x-ms-wmv"),
    WEBM("webm", "video/webm"),
    OGV("ogv", "video/ogg"),
    OGX("ogx", "application/ogg"),
    // audio
    AAC("aac", "audio/aac"),
    MP3("mp3", "audio/mpeg"),
    OGA("oga", "audio/ogg"),
    WAV("wav", "audio/wav"),
    WEBA("weba", "audio/webm");

    @Getter
    @JsonValue
    private final String value;

    @Getter
    private final String contentType;

    private final String fileExtension;

    AssetType(String value, String contentType) {
        this(value, contentType, null);
    }

    AssetType(String value, String contentType, String fileExtension) {
        this.value = value;
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
