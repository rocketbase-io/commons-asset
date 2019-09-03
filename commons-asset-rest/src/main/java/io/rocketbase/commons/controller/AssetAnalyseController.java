package io.rocketbase.commons.controller;

import io.rocketbase.commons.dto.asset.AssetMeta;
import io.rocketbase.commons.dto.batch.AssetBatchAnalyseResult;
import io.rocketbase.commons.service.AssetBatchService;
import io.rocketbase.commons.service.AssetService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

@RestController
@ConditionalOnExpression(value = "${asset.api.analyse:true}")
@RequestMapping("${asset.api:/api/asset}")
@Slf4j
public class AssetAnalyseController implements BaseAssetController {

    @Resource
    private AssetService assetService;

    @Resource
    private AssetBatchService assetBatchService;

    @SneakyThrows
    @RequestMapping(value = "/analyse", method = RequestMethod.POST)
    public AssetMeta analyseFile(@RequestParam("file") MultipartFile file) {
        File tempFile = null;
        try {
            tempFile = File.createTempFile("asset", "");
            IOUtils.copy(file.getInputStream(), new FileOutputStream(tempFile));

            return assetService.analyse(tempFile, file.getOriginalFilename());
        } finally {
            if (tempFile != null) {
                tempFile.delete();
            }
        }
    }

    @SneakyThrows
    @RequestMapping(value = "/analyse/batch", method = RequestMethod.POST)
    public AssetBatchAnalyseResult processBatchAnalyse(@RequestBody List<String> urls) {
        return assetBatchService.batchAnalyse(urls);
    }


}
