package io.rocketbase.commons.dto.asset;

import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Schema(enumAsRef = true)
public enum PreviewSize implements PreviewParameter {
    @Schema(description = "150x150 pixels")
    XS("xs", 150, 150, 0.7f),
    @Schema(description = "300x300 pixels")
    S("s", 300, 300, 0.75f),
    @Schema(description = "600x600 pixels")
    M("m", 600, 600, 0.8f),
    @Schema(description = "1200x1200 pixels")
    L("l", 1200, 1200, 0.85f),
    @Schema(description = "1900x1900 pixels")
    XL("xl", 1900, 1900, 0.85f);

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }

    @Getter
    private final int maxWidth;

    @Getter
    private final int maxHeight;

    @Getter
    private final float defaultQuality;

    public static final PreviewSize getDefault() {
        return S;
    }

    public static List<PreviewSize> getBiggerAs(PreviewSize size) {
        return Arrays.asList(values())
                .stream()
                .filter(v -> v.ordinal() > size.ordinal())
                .collect(Collectors.toList());
    }

    public static List<PreviewSize> getSmallerAs(PreviewSize size) {
        return Arrays.asList(values())
                .stream()
                .filter(v -> v.ordinal() < size.ordinal())
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }

    public static PreviewSize getByName(String name, PreviewSize fallback) {
        if (name != null) {
            for (PreviewSize v : values()) {
                if (name.trim().equalsIgnoreCase(v.name())) {
                    return v;
                }
            }
        }
        return fallback;
    }

    /**
     * used as prefix for stores previews in storage-engine
     *
     * @return
     */
    public String getPreviewStoragePath() {
        return "prev_" + name().toLowerCase();
    }

}
