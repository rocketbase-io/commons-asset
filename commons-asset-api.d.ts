export interface AssetAnalyse extends AssetMeta {
    type: AssetType;
}

export interface AssetMeta {
    created: string;
    originalFilename: string;
    fileSize: number;
    resolution: Resolution;
    colorPalette: ColorPalette;
    referenceUrl: string;
}

export interface AssetPreviews {
    XS?: string;
    S?: string;
    M?: string;
    L?: string;
    XL?: string;
}

export interface AssetRead extends AssetReference {
    previews: AssetPreviews;
    download: string;
    keyValues: Record<string, string>;
}

export interface AssetReference extends AssetReferenceType {
    lqip: string;
}

export interface AssetReferenceType {
    context: string;
    id: string;
    type: AssetType;
    meta: AssetMeta;
    systemRefId: string;
    urlPath: string;
}

export interface AssetUpdate {
    keyValues: Record<string, string>;
}

export interface ColorPalette {
    primary: string;
    colors: string[];
}

export interface QueryAsset {
    before: string;
    after: string;
    originalFilename: string;
    referenceUrl: string;
    context: string;
    types: AssetType[];
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
    entries: AssetBatchWriteEntry[];
}

export interface AssetBatchWriteEntry {
    url: string;
    systemRefId: string;
    context: string;
    keyValues: Record<string, string>;
}

export type AssetType =
    "JPEG"
    | "PNG"
    | "GIF"
    | "TIFF"
    | "PDF"
    | "ZIP"
    | "XLS"
    | "XLSX"
    | "DOC"
    | "DOCX"
    | "PPT"
    | "PPTX";

export type PreviewSize = "XS" | "S" | "M" | "L" | "XL";

export type AssetErrorCodes =
    "INVALID_CONTENT_TYPE"
    | "NOT_ALLOWED_CONTENT_TYPE"
    | "ASSET_FILE_IS_EMPTY"
    | "SYSTEM_REF_ID_ALREADY_USED"
    | "UNPROCESSABLE_ASSET"
    | "NOT_DOWNLOADABLE";
