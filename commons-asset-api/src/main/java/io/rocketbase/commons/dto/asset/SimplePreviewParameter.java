package io.rocketbase.commons.dto.asset;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SimplePreviewParameter implements PreviewParameter {

    private final int maxWidth;

    private final int maxHeight;

    private final float defaultQuality;
}
