package io.rocketbase.commons.tooling;

import de.androidpit.colorthief.ColorThief;
import de.androidpit.colorthief.MMCQ;
import de.androidpit.colorthief.RGBUtil;
import io.rocketbase.commons.dto.asset.ColorPalette;
import lombok.NonNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

public final class ColorDetection {

    public static ColorPalette detect(@NonNull File file) throws IOException {
        return detect(ImageIO.read(file));
    }

    public static ColorPalette detect(@NonNull InputStream inputStream) throws IOException {
        return detect(ImageIO.read(inputStream));
    }

    public static ColorPalette detect(@NonNull BufferedImage bufferedImage) {
        MMCQ.CMap colorMap = ColorThief.getColorMap(bufferedImage, 4);
        if (colorMap != null && !colorMap.vboxes.isEmpty()) {
            List<String> colors = colorMap.vboxes.stream()
                    .map(v -> RGBUtil.createRGBHexString(v.avg(false)))
                    .collect(Collectors.toList());
            return new ColorPalette(colors.get(0), colors.size() > 1 ? colors.subList(1, colors.size() - 1).stream().distinct().collect(Collectors.toList()) : null);
        }
        return null;
    }
}
