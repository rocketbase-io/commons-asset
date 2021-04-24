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
    sizes?: string;
    srcset?: string;
    src: string;
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
 * a short representation of {@link AssetRead} in order to reduce response values for rendering an asset within an application
 */
export interface AssetDisplay {
    id: string;
    type: AssetType;
    meta: AssetMeta;
    image?: ResponsiveImage;
    download?: string;
    lqip?: string;
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

export interface ResponsiveImage {
    sizes?: string;
    srcset?: string;
    src: string;
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

export function getMimeType(type: AssetType): string {
    switch (type) {
        case "jpeg": return "image/jpeg";
        case "png": return "image/png";
        case "apng": return "image/apng";
        case "gif": return "image/gif";
        case "tiff": return "image/tiff";
        case "bmp": return "image/bmp";
        case "ico": return "image/x-ico";
        case "svg": return "image/svg+xml";
        case "webp": return "image/webp";
        case "heif": return "image/heif";
        case "heic": return "image/heic";
        case "pdf": return "application/pdf";
        case "zip": return "application/zip";
        case "tar": return "application/x-tar";
        case "rar": return "application/vnd.rar";
        case "gzip": return "application/gzip";
        case "7z": return "application/x-7z-compressed";
        case "xls": return "application/msexcel";
        case "xlsx": return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        case "doc": return "application/msword";
        case "docx": return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        case "ppt": return "application/vnd.ms-powerpoint";
        case "pptx": return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
        case "odp": return "application/vnd.oasis.opendocument.presentation";
        case "ods": return "application/vnd.oasis.opendocument.spreadsheet";
        case "odt": return "application/vnd.oasis.opendocument.text";
        case "csv": return "application/csv";
        case "txt": return "application/text";
        case "json": return "application/json";
        case "rtf": return "application/rtf";
        case "xml": return "application/xml";
        case "mpeg": return "video/mpeg";
        case "mp4": return "video/mp4";
        case "mpv": return "video/mpv";
        case "mov": return "video/quicktime";
        case "avi": return "video/x-msvideo";
        case "wmv": return "video/x-ms-wmv";
        case "webm": return "video/webm";
        case "ogv": return "video/ogg";
        case "ogx": return "application/ogg";
        case "aac": return "audio/aac";
        case "mp3": return "audio/mpeg";
        case "oga": return "audio/ogg";
        case "wav": return "audio/wav";
        case "weba": return "audio/webm";
        default: return "application/octet-stream";
    }
}

export function isImage(type: AssetType): boolean {
    const mimeType = getMimeType(type);
    return mimeType.lastIndexOf("image", 0) === 0;
}

export function getMaximumSize(size: PreviewSize): number {
    switch (size) {
        case "xs": return 150;
        case "s": return 300;
        case "m": return 600;
        case "l": return 1200;
        case "xl": return 1900;
        default: return -1;
    }
}