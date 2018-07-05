package io.rocketbase.commons.resource;

import com.google.common.io.ByteStreams;
import io.rocketbase.commons.dto.PageableResult;
import io.rocketbase.commons.dto.asset.AssetRead;
import io.rocketbase.commons.dto.asset.QueryAsset;
import io.rocketbase.commons.dto.batch.AssetBatchResult;
import io.rocketbase.commons.dto.batch.AssetBatchWrite;
import io.rocketbase.commons.exception.NotFoundException;
import lombok.SneakyThrows;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.InputStream;
import java.time.format.DateTimeFormatter;

public class AssetResource {

    protected RestTemplate restTemplate;
    protected String baseUrl;

    public AssetResource(String baseUrl) {
        this(baseUrl, null);
    }

    public AssetResource(String baseUrl, RestTemplate restTemplate) {
        Assert.hasText(baseUrl, "baseUrl is required");
        this.baseUrl = baseUrl;
        this.restTemplate = restTemplate;
    }

    protected RestTemplate getRestTemplate() {
        if (restTemplate == null) {
            restTemplate = new RestTemplate();
            restTemplate.setErrorHandler(new BasicResponseErrorHandler());
        }
        return restTemplate;
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

    /**
     * list all available assets
     *
     * @param page     starts by 0
     * @param pagesize max 50
     * @return pageable assetData
     */
    public PageableResult<AssetRead> findAll(int page, int pagesize, QueryAsset query) {
        UriComponentsBuilder uriBuilder = getUriBuilder()
                .queryParam("page", page)
                .queryParam("size", pagesize);
        if (query != null) {
            if (query.getBefore() != null) {
                uriBuilder.queryParam("before", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(query.getBefore()));
            }
            if (query.getAfter() != null) {
                uriBuilder.queryParam("after", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(query.getAfter()));
            }
            if (query.getOriginalFilename() != null) {
                uriBuilder.queryParam("originalFilename", query.getOriginalFilename());
            }
            if (query.getReferenceUrl() != null) {
                uriBuilder.queryParam("referenceUrl", query.getReferenceUrl());
            }
            if (query.getTypes() != null) {
                uriBuilder.queryParam("type", query.getTypes());
            }
        }
        return query(uriBuilder);
    }

    private PageableResult<AssetRead> query(UriComponentsBuilder uriBuilder) {
        ResponseEntity<PageableResult<AssetRead>> response = getRestTemplate().exchange(uriBuilder.toUriString(),
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
            response = getRestTemplate().exchange(getUriBuilder().path("/" + sid)
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
        } catch (NotFoundException e) {
            return null;
        }

        return response.getBody();
    }

    /**
     * delete asset from filesystem and database
     *
     * @param id assetId
     */
    public void delete(String id) {
        ResponseEntity<Void> response = getRestTemplate().exchange(getUriBuilder().path("/" + id)
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
        ResponseEntity<AssetRead> response = getRestTemplate().exchange(getUriBuilder().toUriString(),
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
        ResponseEntity<AssetBatchResult> response = getRestTemplate().exchange(getUriBuilder().path("/batch")
                        .toUriString(),
                HttpMethod.POST,
                new HttpEntity<>(assetBatch),
                AssetBatchResult.class);

        return response.getBody();
    }


    protected UriComponentsBuilder getUriBuilder() {
        return UriComponentsBuilder.fromUriString(baseUrl)
                .path("/api/asset");
    }
}
