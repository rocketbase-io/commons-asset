package io.rocketbase.commons.dto.asset;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.time.Instant;
import java.util.Collection;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryAsset implements Serializable {

    @Nullable
    private Instant before;

    @Nullable
    private Instant after;

    @Nullable
    private String originalFilename;

    /**
     * search exact match
     */
    @Nullable
    private String systemRefId;

    /**
     * in mongo-implementation it's a regex "like" search<br>
     * in mysql it's an exact hash compare (limitations within mysql of column/index length)
     */
    @Nullable
    private String referenceUrl;

    /**
     * search exact match
     */
    @Nullable
    private String context;

    @Nullable
    private Collection<AssetType> types;

    /**
     * true: queries all assets that has an eol value<br>
     * false: all without<br>
     * null means ignore
     */
    @Nullable
    private Boolean hasEolValue;

    /**
     * true: queries all assets that has an eol value that is expired<br>
     * false: all without or newer then now<br>
     * null means ignore
     */
    @Nullable
    private Boolean isEol;

    /**
     * search for given key and value with exact match ignore cases
     */
    @Nullable
    private Map<String, String> keyValues;
}
