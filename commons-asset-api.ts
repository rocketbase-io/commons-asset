// Generated using typescript-generator version 2.27.744 on 2020-12-11 21:00:38.

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
    fileSizeHumanReadable: string;
}

export interface ResponsiveImage {
    sizes: string;
    srcset: string;
    src?: string;
}

export interface AssetPreviews {
    xs?: string;
    s?: string;
    m?: string;
    l?: string;
    xl?: string;
    responsive?: ResponsiveImage;
}

export interface AssetRead extends AssetReference, rest.HasKeyValue {
    previews: AssetPreviews;
    download?: string;
    keyValues?: Record<string, string>;
    eol?: string;
    created: string;
    modifiedBy: string;
    modified: string;
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

export interface QueryAsset {
    before?: string;
    after?: string;
    originalFilename?: string;
    systemRefId?: string;
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

export type AssetType = "jpeg" | "png" | "apng" | "gif" | "tiff" | "bmp" | "ico" | "svg" | "webp" | "heif" | "heic" | "pdf" | "zip" | "tar" | "rar" | "gzip" | "7z" | "xls" | "xlsx" | "doc" | "docx" | "ppt" | "pptx" | "odp" | "ods" | "odt" | "csv" | "txt" | "json" | "rtf" | "xml" | "mpeg" | "mp4" | "mpv" | "mov" | "avi" | "wmv" | "webm" | "ogv" | "ogx" | "aac" | "mp3" | "oga" | "wav" | "weba";

export type PreviewSize = "xs" | "s" | "m" | "l" | "xl";

export type AssetErrorCodes = "invalid_content_type" | "not_allowed_content_type" | "asset_file_is_empty" | "system_ref_id_already_used" | "unprocessable_asset" | "not_downloadable";
