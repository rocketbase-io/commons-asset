package io.rocketbase.commons.resource;

import com.google.common.io.ByteStreams;
import io.rocketbase.commons.dto.PageableResult;
import io.rocketbase.commons.dto.asset.AssetRead;
import io.rocketbase.commons.dto.batch.AssetBatchResult;
import io.rocketbase.commons.dto.batch.AssetBatchWrite;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.InputStream;

public class AssetResource {

    private RestTemplate restTemplate;

    private String baseDomain;

    public AssetResource(RestTemplate restTemplate, @Value("${asset.rest.url:}") String baseDomain) {
        this.restTemplate = restTemplate;
        this.baseDomain = baseDomain;
    }

    /**
     * list all available assets
     *
     * @param page     starts by 0
     * @param pagesize max 50
     * @return pageable assetData
     */
    public PageableResult<AssetRead> findAll(int page, int pagesize) {
        UriComponentsBuilder uriBuilder = getUriBuilder()
                .queryParam("page", page)
                .queryParam("size", pagesize);
        return query(uriBuilder);
    }

    private PageableResult<AssetRead> query(UriComponentsBuilder uriBuilder) {
        ResponseEntity<PageableResult<AssetRead>> response = restTemplate.exchange(uriBuilder.toUriString(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<PageableResult<AssetRead>>() {
                });

        return response.getBody();
    }

    /**
     * search for asset by id or systemRefId
     *
     * @param sid assetId or systemRefId
     * @return AssetData
     */
    public AssetRead find(String sid) {
        ResponseEntity<AssetRead> response;
        try {
            response = restTemplate.exchange(getUriBuilder().path("/" + sid)
                            .toUriString(),
                    HttpMethod.GET,
                    null,
                    AssetRead.class);
        } catch (HttpClientErrorException e) {
            if (HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
                return null;
            } else {
                throw e;
            }
        }

        return response.getBody();
    }

    /**
     * delete asset from filesystem and database
     *
     * @param id assetId
     */
    public void delete(String id) {
        ResponseEntity<Void> response = restTemplate.exchange(getUriBuilder().path(id)
                        .toUriString(),
                HttpMethod.DELETE,
                null, Void.class);
    }

    /**
     * upload binary file to asset endpoint
     *
     * @param assetResource stream to resource
     * @param filename      originalFileName
     * @return stored asset references
     */
    public AssetRead uploadFile(InputStream assetResource, String filename) {
        return uploadFile(assetResource, filename, null);
    }

    /**
     * upload binary file to asset endpoint
     *
     * @param assetResource stream to resource
     * @param filename      optional originalFileName
     * @param systemRefId   optional reference id
     * @return stored asset references
     */
    @SneakyThrows
    public AssetRead uploadFile(InputStream assetResource, String filename, String systemRefId) {
        byte[] bytes = ByteStreams.toByteArray(assetResource);
        LinkedMultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("file", new ByteArrayResource(bytes) {
            @Override
            public String getFilename() {
                return filename != null && !filename.isEmpty() ? filename : super.getFilename();
            }
        });
        // optional parameters
        if (systemRefId != null) {
            form.add("systemRefId", systemRefId);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(form, headers);
        ResponseEntity<AssetRead> response = restTemplate.exchange(getUriBuilder().toUriString(),
                HttpMethod.POST,
                requestEntity,
                AssetRead.class);

        return response.getBody();
    }

    /**
     * download assets in batch and store them
     *
     * @param assetBatch list of url's to download and keep
     * @return AssetBatchResultData
     */
    public AssetBatchResult processBatchFileUrls(AssetBatchWrite assetBatch) {
        ResponseEntity<AssetBatchResult> response = restTemplate.exchange(getUriBuilder().path("/batch")
                        .toUriString(),
                HttpMethod.POST,
                new HttpEntity<>(assetBatch),
                AssetBatchResult.class);

        return response.getBody();
    }


    protected UriComponentsBuilder getUriBuilder() {
        return UriComponentsBuilder.fromUriString(baseDomain)
                .path("/api/asset");
    }
}
