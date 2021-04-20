package io.rocketbase.commons.config;

import io.rocketbase.commons.controller.*;
import io.rocketbase.commons.controller.exceptionhandler.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

@Configuration
@EnableConfigurationProperties({AssetApiProperties.class})
@RequiredArgsConstructor
public class AssetServerAutoConfiguration implements Serializable {

    private final AssetApiProperties assetApiProperties;

    // -------------------------------------------------------
    // --------------------- Controller ----------------------
    // -------------------------------------------------------

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnExpression(value = "${asset.api.analyse:true}")
    public AssetAnalyseController assetAnalyseController() {
        return new AssetAnalyseController();
    }

    @Bean
    @ConditionalOnMissingBean
    public AssetBaseController assetBaseController() {
        return new AssetBaseController();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnExpression(value = "${asset.api.batch:true}")
    public AssetBatchController assetBatchController() {
        return new AssetBatchController();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnExpression(value = "${asset.api.copy:true}")
    public AssetCopyController assetCopyController() {
        return new AssetCopyController();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnExpression(value = "${asset.api.delete:true}")
    public AssetDeleteController assetDeleteController() {
        return new AssetDeleteController();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnExpression(value = "${asset.api.download:true}")
    public AssetDownloadController assetDownloadController() {
        return new AssetDownloadController();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnExpression(value = "${asset.api.preview:true}")
    public AssetPreviewController assetPreviewController() {
        return new AssetPreviewController(assetApiProperties);
    }

    // -------------------------------------------------------
    // ------------------ ExceptionHandlers ------------------
    // -------------------------------------------------------

    @Bean
    @ConditionalOnMissingBean
    public EmptyFileExceptionHandler emptyFileExceptionHandler() {
        return new EmptyFileExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public InvalidContentTypeExceptionHandler invalidContentTypeExceptionHandler() {
        return new InvalidContentTypeExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public NotAllowedAssetTypeExceptionHandler notAllowedAssetTypeExceptionHandler() {
        return new NotAllowedAssetTypeExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public UnprocessableAssetExceptionHandler unprocessableAssetExceptionHandler() {
        return new UnprocessableAssetExceptionHandler();
    }

}
