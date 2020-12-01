package io.rocketbase.commons.dto.asset;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.rocketbase.commons.serializer.AssetPreviewsDeserializer;
import io.rocketbase.commons.serializer.AssetPreviewsSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize(using = AssetPreviewsSerializer.class)
@JsonDeserialize(using = AssetPreviewsDeserializer.class)
public class AssetPreviews implements Serializable {

    /**
     * is filled according to configuration<br>
     * please use AssetPreviews:getPreview
     */
    private Map<PreviewSize, String> previewMap;

    /**
     * calculated Responsive Image Breakpoints
     */
    @Nullable
    private ResponsiveImage responsive;

    /**
     * return's preview of given size <br>
     * when not found tries to catch bigger or when no success smaller one
     *
     * @param size given size
     * @return url or null
     */
    @JsonIgnore
    public String getPreview(PreviewSize size) {
        if (previewMap.containsKey(size)) {
            return previewMap.get(size);
        } else {
            for (PreviewSize biggerSize : PreviewSize.getBiggerAs(size)) {
                if (previewMap.containsKey(biggerSize)) {
                    return previewMap.get(biggerSize);
                }
            }
            for (PreviewSize smallerSize : PreviewSize.getSmallerAs(size)) {
                if (previewMap.containsKey(smallerSize)) {
                    return previewMap.get(smallerSize);
                }
            }
        }
        return null;
    }

    public AssetPreviews add(PreviewSize size, String url) {
        if (previewMap == null) {
            previewMap = new TreeMap<>();
        }
        previewMap.put(size, url);
        return this;
    }

    public AssetPreviews withResponsive(ResponsiveImage responsive) {
        setResponsive(responsive);
        return this;
    }
}
