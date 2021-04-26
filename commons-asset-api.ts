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

export type PreviewSize = "xs" | "s" | "m" | "l" | "xl";

export type AssetErrorCodes =
    "invalid_content_type"
    | "not_allowed_content_type"
    | "asset_file_is_empty"
    | "system_ref_id_already_used"
    | "unprocessable_asset"
    | "not_downloadable";


const mimeTypes = {
    jpeg: "image/jpeg",
    png: "image/png",
    apng: "image/apng",
    gif: "image/gif",
    tiff: "image/tiff",
    bmp: "image/bmp",
    ico: "image/x-ico",
    svg: "image/svg+xml",
    webp: "image/webp",
    heif: "image/heif",
    heic: "image/heic",
    pdf: "application/pdf",
    zip: "application/zip",
    tar: "application/x-tar",
    rar: "application/x-rar-compressed",
    gzip: "application/gzip",
    "7z": "application/x-7z-compressed",
    xls: "application/msexcel",
    xlsx: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
    doc: "application/msword",
    docx: "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
    ppt: "application/vnd.ms-powerpoint",
    pptx: "application/vnd.openxmlformats-officedocument.presentationml.presentation",
    odp: "application/vnd.oasis.opendocument.presentation",
    ods: "application/vnd.oasis.opendocument.spreadsheet",
    odt: "application/vnd.oasis.opendocument.text",
    csv: "text/csv",
    txt: "text/plain",
    rtf: "application/rtf",
    xml: "application/xml",
    mpeg: "video/mpeg",
    mp4: "video/mp4",
    mpv: "video/mpv",
    mov: "video/quicktime",
    avi: "video/x-msvideo",
    wmv: "video/x-ms-wmv",
    webm: "video/webm",
    ogv: "video/ogg",
    ogx: "application/ogg",
    aac: "audio/aac",
    mp3: "audio/mpeg",
    oga: "audio/ogg",
    wav: "audio/wav",
    weba: "audio/webm",
    default: "application/octet-stream",
} as const;

export type AssetType = keyof typeof mimeTypes;

export type SupportedMimeType = (typeof mimeTypes)[AssetType];

export function getMimeType(asset: AssetReference): string {
    if (mimeTypes[asset.type]) return mimeTypes[asset.type];
    return mimeTypes.default;
}

export function isImage(type: AssetType): boolean {
    return mimeTypes[type] ?
        mimeTypes[type].lastIndexOf("image", 0) === 0
        : false;
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