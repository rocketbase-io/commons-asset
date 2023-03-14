import * as rest from "./commons-rest-api";

/**
 * results of the analyse service for assets/download-urls
 */
export interface AssetAnalyse extends AssetMeta {
    type: AssetType;
    /**
     * in case of enabeled lqip contain base64 image preview"
     */
    lqip?: string;
}

export interface AssetMeta {
    created: string;
    /**
     * name of the file during upload-process
     */
    originalFilename: string;
    /**
     * original file size in bytes
     */
    fileSize: number;
    /**
     * only filled in case of image asset
     */
    resolution?: Resolution;
    /**
     * only filled in case of image asset
     */
    colorPalette?: ColorPalette;
    /**
     * only filled in case of batch downloaded image
     */
    referenceUrl?: string;
    /**
     * example 1.5Mb
     */
    fileSizeHumanReadable: string;
}

export interface ResponsiveImage {
    /**
     * layout example: (max-width: 640px) 100vw, 640px
     */
    sizes?: string;
    /**
     * layout example: https://preview/abc_w_300.png 300w, https://preview/abc_w_600.png 600w,  https://preview/abc_w.png 640w
     */
    srcset?: string;
    /**
     * contains the tallest preview url as default src (browser will detect best fitting preview from srcset)
     */
    src: string;
}

export interface AssetPreviews {
    /**
     * 150x150 pixels"
     */
    xs?: string;
    /**
     * 300x300 pixels
     */
    s?: string;
    /**
     * 600x600 pixels
     */
    m?: string;
    /**
     * 1200x1200 pixels
     */
    l?: string;
    /**
     * 1900x1900 pixels
     */
    xl?: string;
    /**
     * calculated Responsive Image Breakpoints
     */
    responsive?: ResponsiveImage;
}

export interface AssetRead extends AssetReference, rest.HasKeyValue {
    previews?: AssetPreviews;
    /**
     * optional property to receive the downloadUrl
     */
    download?: string;
    /**
     * optional keyValuePair that could have been stored related to the asset
     */
    keyValues?: Record<string, string>;
    /**
     * date after that the asset could be deleted
     */
    eol?: string;
    created: string;
    modifiedBy: string;
    modified: string;
}

/**
 * a short representation of {@link AssetRead} in order to reduce response values for rendering an asset within an application
 */
export interface AssetDisplay {
    /**
     * unique id of asset
     */
    id: string;
    /**
     * type of asset
     */
    type: AssetType;
    /**
     * additional information to asset
     */
    meta: AssetMeta;
    /**
     * values to render imag
     */
    image?: ResponsiveImage;
    /**
     * url to download original file -  in case of disabled download this value could be null
     */
    download?: string;
    /**
     * Low Quality Image Placeholder (LQIP) that is a base64 preview in ultra low-res + quality
     */
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
    /**
     * update it with care - needs to get updated in AssetReferences if stored anywhere...
     */
    systemRefId?: string;
    /**
     * will removed key that have value of null
     * will only add/replace new/existing key values
     * not mentioned key will still stay the same
     */
    keyValues?: Record<string, string>;
    /**
     * after this time the asset could get deleted within a cleanup job
     */
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

/**
 * detection results of colors within an image
 */
export interface ColorPalette {
    /**
     * dominant color
     */
    primary: string;
    /**
     * other colors ordered by priority (not containing primary)
     */
    colors: string[];
}

export interface QueryAsset {
    before?: string;
    after?: string;
    originalFilename?: string;
    systemRefId?: string;
    /**
     * in mongo-implementation it's a regex "like" search<br>
     * in mysql it's an exact hash compare (limitations within mysql of column/index length)
     */
    referenceUrl?: string;
    /**
     * search exact match
     */
    context?: string;
    types?: AssetType[];
    /**
     * true: queries all assets that has an eol value<br>
     * false: all without<br>
     * null means ignore
     */
    hasEolValue?: boolean;
    /**
     * true: queries all assets that has an eol value that is expired<br>
     * false: all without or newer then now<br>
     * null means ignore
     */
    isEol?: boolean;
    /**
     * search for given key and value with exact match ignore cases
     */
    keyValues?: Record<string, string>;
}

/**
 * resolution of an image in pixels
 */
export interface Resolution {
    /**
     * width in pixel
     */
    width: number;
    /**
     * height in pixel
     */
    height: number;
}

/**
 * calculated Responsive Image Breakpoints
 */
export interface ResponsiveImage {
    /**
     * layout example: (max-width: 640px) 100vw, 640px
     */
    sizes?: string;
    /**
     * layout example: https://preview/abc_w_300.png 300w, https://preview/abc_w_600.png 600w,  https://preview/abc_w.png 640w
     */
    srcset?: string;
    /**
     * contains the tallest preview url as default src (browser will detect best fitting preview from srcset)
     */
    src: string;
}

/**
 * wrapped batch results for analyse service
 */
export interface AssetBatchAnalyseResult {
    /**
     * key holds the given url. value the result.
     */
    succeeded: Record<string, AssetAnalyse>;
    /**
     * key holds the given url. value the result.
     */
    failed: Record<string, AssetErrorCodes>;
}

/**
 * wrapped batch results for store service
 */
export interface AssetBatchResult {
    /**
     * key holds the given url. value the result.
     */
    succeeded: Record<string, AssetRead>;
    /**
     * key holds the given url. value the result.
     */
    failed: Record<string, AssetErrorCodes>;
}

/**
 * wrapped batch results for store service without previews
 */
export interface AssetBatchResultWithoutPreviews {
    /**
     * key holds the given url. value the result.
     */
    succeeded: Record<string, AssetReference>;
    /**
     * key holds the given url. value the result.
     */
    failed: Record<string, AssetErrorCodes>;
}

export interface AssetBatchWrite {
    /**
     * when enabled pre check if downloadUrl has already been downloaded - then take it
     */
    useCache?: boolean;
    /**
     * list of urls with additional information that will be stored in succeeded case.
     */
    entries: AssetBatchWriteEntry[];
}

/**
 * detailed instruction for each url
 */
export interface AssetBatchWriteEntry extends AssetUploadMeta {
    /**
     * full qualified url to the asset that should been analysed/stored
     */
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
    avif: "image/avif",
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
    glb: "model/gltf-binary",
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
        case "xs":
            return 150;
        case "s":
            return 300;
        case "m":
            return 600;
        case "l":
            return 1200;
        case "xl":
            return 1900;
        default:
            return -1;
    }
}
