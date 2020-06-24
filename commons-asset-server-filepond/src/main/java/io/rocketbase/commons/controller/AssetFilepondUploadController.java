package io.rocketbase.commons.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.rocketbase.commons.converter.AssetConverter;
import io.rocketbase.commons.dto.asset.DefaultAssetUploadMeta;
import io.rocketbase.commons.exception.EmptyFileException;
import io.rocketbase.commons.model.AssetEntity;
import io.rocketbase.commons.service.AssetRepository;
import io.rocketbase.commons.service.AssetService;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import java.io.InputStream;

@RestController
@RequestMapping("${asset.api:/api/asset}")
@Slf4j
public class AssetFilepondUploadController implements BaseController {

    @Resource
    private AssetRepository assetRepository;

    @Resource
    private AssetConverter assetConverter;

    @Resource
    private AssetService assetService;

    @Resource
    private ObjectMapper objectMapper;

    @SneakyThrows
    @RequestMapping(method = RequestMethod.POST, value = "/filepond")
    public String handleFileUpload(MultipartHttpServletRequest request, @RequestParam(required = false, name = "parameter", defaultValue = "filepond") String parameter) {
        MultipartFile file = request.getMultiFileMap().getFirst(parameter);
        DefaultAssetUploadMeta meta = objectMapper.readValue(request.getParameter(parameter), DefaultAssetUploadMeta.class);
        if (file.isEmpty()) {
            throw new EmptyFileException();
        }
        @Cleanup InputStream inputStream = file.getInputStream();
        AssetEntity asset = assetService.store(inputStream, file.getOriginalFilename(), file.getSize(), null, meta);
        return asset.getId();
    }
}
