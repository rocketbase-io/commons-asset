package io.rocketbase.commons.dto.asset;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public enum PreviewSize {
    XS(150, 150), S(300, 300), M(600, 600), L(1200, 1200), XL(1900, 1900);

    @Getter
    private final int maxWidth;

    @Getter
    private final int maxHeight;

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
                if (name.equalsIgnoreCase(v.name())) {
                    return v;
                }
            }
        }
        return fallback;
    }

}
