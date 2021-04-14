package io.rocketbase.commons.controller;

import io.rocketbase.commons.service.AssetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@ConditionalOnExpression(value = "${asset.api.delete:true}")
@RequestMapping("${asset.api:/api/asset}")
@Slf4j
public class AssetDeleteController implements BaseController {

    @Resource
    private AssetService assetService;

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteAsset(@PathVariable("id") String id) {
        assetService.deleteById(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
