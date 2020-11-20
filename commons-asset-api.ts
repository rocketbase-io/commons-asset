import * as rest from "./commons-rest-api.d.ts";

export interface AssetAnalyse extends AssetMeta {
    type: AssetType;
    lqip: string;
}

export interface AssetMeta {
    created: string;
    originalFilename: string;
    fileSize: number;
    resolution?: Resolution;
    colorPalette?: ColorPalette;
    referenceUrl?: string;
}

export interface AssetPreviews {
    XS?: string;
    S?: string;
    M?: string;
    L?: string;
    XL?: string;
}

export interface AssetRead extends AssetReference, rest.HasKeyValue {
    previews: AssetPreviews;
    download?: string;
    keyValues?: Record<string, string>;
    eol?: string;
}

/**
 * used to store reference in db or elsewhere
 *
 * could be converted to AssetRead without database access
 */
export interface AssetReference {
    /**
     * allows to store individual grouping for assets to find all picture of a flexible type
     *
     * for example all avatar images or backgrounds...
     */
    context?: string;
    /**
     * reference to asset in asset collection
     */
    id: string;
    type: AssetType;
    /**
     * Low Quality Image Placeholder (LQIP) that is a base64 preview in ultra low-res + quality
     */
    lqip?: string;
    /**
     * optional foreign id of other system
     */
    systemRefId?: string;
    /**
     * relative path of storage
     */
    urlPath: string;
    /**
     * Low Quality Image Placeholder (LQIP) that is a base64 preview in ultra low-res + quality
     */
    meta: AssetMeta;
}

/**
 * null properties mean let value as it is
 */
export interface AssetUpdate {
    systemRefId?: string;
    keyValues?: Record<string, string>;
    eol?: string;
}

export interface AssetUploadMeta {
    /**
     * optional - name of context (could be used to differ buckets for example)
     */
    context?: string;
    /**
     * optional - will get stored with lowercase
     *
     * max length of 50 characters
     *
     * key with _ as prefix will not get displayed in REST_API
     */
    keyValues?: Record<string, string>;
    /**
     * optional - after this time the asset could get deleted within a cleanup job
     */
    eol?: string;
    /**
     * optional - reference id (needs to be unique within system)
     */
    systemRefId?: string;
}

export interface ColorPalette {
    primary: string;
    colors: string[];
}

export interface PreviewParameter {
    defaultQuality: number;
    maxWidth: number;
    maxHeight: number;
}

export interface QueryAsset {
    before?: string;
    after?: string;
    originalFilename?: string;
    referenceUrl?: string;
    context?: string;
    types?: AssetType[];
    hasEolValue?: boolean;
    isEol?: boolean;
    keyValues?: Record<string, string>;
}

export interface Resolution {
    width: number;
    height: number;
}

export interface AssetBatchAnalyseResult {
    succeeded: Record<string, AssetAnalyse>;
    failed: Record<string, AssetErrorCodes>;
}

export interface AssetBatchResult {
    succeeded: Record<string, AssetRead>;
    failed: Record<string, AssetErrorCodes>;
}

export interface AssetBatchResultWithoutPreviews {
    succeeded: Record<string, AssetReference>;
    failed: Record<string, AssetErrorCodes>;
}

export interface AssetBatchWrite {
    useCache?: boolean;
    entries: AssetBatchWriteEntry[];
}

export interface AssetBatchWriteEntry extends AssetUploadMeta {
    url: string;
}

export type AssetErrorCodes = "INVALID_CONTENT_TYPE" | "NOT_ALLOWED_CONTENT_TYPE" | "ASSET_FILE_IS_EMPTY" | "SYSTEM_REF_ID_ALREADY_USED" | "UNPROCESSABLE_ASSET" | "NOT_DOWNLOADABLE";

export type AssetType = "JPEG" | "PNG" | "APNG" | "GIF" | "TIFF" | "BMP" | "ICO" | "SVG" | "WEBP" | "HEIF" | "HEIC" | "PDF" | "ZIP" | "CSV" | "XLS" | "XLSX" | "DOC" | "DOCX" | "PPT" | "PPTX";

export type PreviewSize = "XS" | "S" | "M" | "L" | "XL";
