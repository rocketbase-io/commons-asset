export interface AssetAnalyse extends AssetMeta {
    type: AssetType;
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

export interface AssetRead extends AssetReference {
    previews: AssetPreviews;
    download?: string;
    keyValues?: Record<string, string>;
}

/**
 * used to store reference in db
 */
export interface AssetReference extends AssetReference {
    lqip?: string;
}

export interface AssetReference {
    context: string;
    id: string;
    type: AssetType;
    systemRefId?: string;
    urlPath: string;
    meta: AssetMeta;
}

/**
 * null properties mean let value as it is
 */
export interface AssetUpdate {
    keyValues: Record<string, string>;
}

export interface ColorPalette {
    primary: string;
    colors: string[];
}

export interface QueryAsset {
    before?: string;
    after?: string;
    originalFilename?: string;
    referenceUrl?: string;
    context?: string;
    types?: AssetType[];
}

export interface Resolution {
    width: number;
    height: number;
}

export interface AssetBatchAnalyseResult {
    succeeded: Record<string, string>;
    failed: Record<string, string>;
}

export interface AssetBatchResult {
    succeeded: Record<string, string>;
    failed: Record<string, string>;
}

export interface AssetBatchResultWithoutPreviews {
    succeeded: Record<string, string>;
    failed: Record<string, string>;
}

export interface AssetBatchWrite {
    entries: AssetBatchWriteEntry[];
}

export interface AssetBatchWriteEntry {
    url: string;
    systemRefId?: string;
    context: string;
    keyValues?: Record<string, string>;
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
