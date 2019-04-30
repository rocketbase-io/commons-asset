package io.rocketbase.commons.config;

import io.rocketbase.commons.controller.*;
import io.rocketbase.commons.controller.exceptionhandler.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

@Configuration
public class AssetServerAutoConfiguration implements Serializable {

    // -------------------------------------------------------
    // --------------------- Controller ----------------------
    // -------------------------------------------------------

    @Bean
    @ConditionalOnMissingBean
    public AssetBaseController assetBaseController() {
        return new AssetBaseController();
    }

    @Bean
    @ConditionalOnMissingBean
    public AssetBatchController assetBatchController() {
        return new AssetBatchController();
    }

    @Bean
    @ConditionalOnMissingBean
    public AssetDeleteController assetDeleteController() {
        return new AssetDeleteController();
    }

    @Bean
    @ConditionalOnMissingBean
    public AssetDownloadController assetDownloadController() {
        return new AssetDownloadController();
    }

    @Bean
    @ConditionalOnMissingBean
    public AssetPreviewController assetPreviewController() {
        return new AssetPreviewController();
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
    public SystemRefIdAlreadyUsedExceptionHandler systemRefIdAlreadyUsedExceptionHandler() {
        return new SystemRefIdAlreadyUsedExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public UnprocessableAssetExceptionHandler unprocessableAssetExceptionHandler() {
        return new UnprocessableAssetExceptionHandler();
    }

}
