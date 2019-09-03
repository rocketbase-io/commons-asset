package io.rocketbase.commons.resource;

import com.google.common.io.ByteStreams;
import io.rocketbase.commons.dto.PageableResult;
import io.rocketbase.commons.dto.asset.AssetAnalyse;
import io.rocketbase.commons.dto.asset.AssetRead;
import io.rocketbase.commons.dto.asset.QueryAsset;
import io.rocketbase.commons.dto.batch.AssetBatchAnalyseResult;
import io.rocketbase.commons.dto.batch.AssetBatchResult;
import io.rocketbase.commons.dto.batch.AssetBatchWrite;
import io.rocketbase.commons.exception.NotFoundException;
import lombok.SneakyThrows;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AssetResource implements BaseRestResource {

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
        return findAll(page, pagesize, null);
    }

    /**
     * list all available assets found by query
     *
     * @param page     starts by 0
     * @param pagesize max 50
     * @return pageable assetData
     */
    public PageableResult<AssetRead> findAll(int page, int pagesize, QueryAsset query) {
        return findAll(PageRequest.of(page, pagesize), query);
    }

    public PageableResult<AssetRead> findAll(Pageable pageable, QueryAsset query) {
        UriComponentsBuilder uriBuilder = appendParams(getUriBuilder(), pageable);
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
            if (query.getContext() != null) {
                uriBuilder.queryParam("context", query.getContext());
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
        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = buildUploadMultipartForm(assetResource, filename, systemRefId);
        ResponseEntity<AssetRead> response = getRestTemplate().exchange(getUriBuilder().toUriString(),
                HttpMethod.POST,
                requestEntity,
                AssetRead.class);

        return response.getBody();
    }

    private HttpEntity<LinkedMultiValueMap<String, Object>> buildUploadMultipartForm(InputStream assetResource, String filename, String systemRefId) throws IOException {
        byte[] bytes = ByteStreams.toByteArray(assetResource);
        LinkedMultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("file", new ByteArrayResource(bytes) {
            @Override
            public String getFilename() {
                return !StringUtils.isEmpty(filename) ? filename : super.getFilename();
            }
        });
        // optional parameters
        if (systemRefId != null) {
            form.add("systemRefId", systemRefId);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        return new HttpEntity<>(form, headers);
    }

    /**
     * download assets in batch and store them
     *
     * @param assetBatch list of url's to download and keep
     * @return AssetBatchResult
     */
    public AssetBatchResult processBatchFileUrls(AssetBatchWrite assetBatch) {
        ResponseEntity<AssetBatchResult> response = getRestTemplate().exchange(getUriBuilder().path("/batch")
                        .toUriString(),
                HttpMethod.POST,
                new HttpEntity<>(assetBatch),
                AssetBatchResult.class);

        return response.getBody();
    }

    public File downloadAsset(String sid) {
        File file = getRestTemplate().execute(getUriBuilder().path("/" + sid).path("/b")
                .toUriString(), HttpMethod.GET, null, clientHttpResponse -> {
            File ret = File.createTempFile("download", "tmp");
            StreamUtils.copy(clientHttpResponse.getBody(), new FileOutputStream(ret));
            return ret;
        });
        return file;
    }

    /**
     * download assets in batch and store them
     *
     * @param urls list of url's to download and analyse
     * @return AssetBatchAnalyseResult
     */
    public AssetBatchAnalyseResult processBatchAnalyseUrls(List<String> urls) {
        ResponseEntity<AssetBatchAnalyseResult> response = getRestTemplate().exchange(getUriBuilder().path("/analyse/batch")
                        .toUriString(),
                HttpMethod.POST,
                new HttpEntity<>(urls),
                AssetBatchAnalyseResult.class);

        return response.getBody();
    }

    /**
     * upload binary file to analyse content
     *
     * @param assetResource stream to resource
     * @return stored asset references
     */
    @SneakyThrows
    public AssetAnalyse analyseFile(InputStream assetResource, String filename) {
        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = buildUploadMultipartForm(assetResource, filename, null);
        ResponseEntity<AssetAnalyse> response = getRestTemplate().exchange(getUriBuilder().path("/analyse").toUriString(),
                HttpMethod.POST,
                requestEntity,
                AssetAnalyse.class);

        return response.getBody();
    }

    protected UriComponentsBuilder getUriBuilder() {
        return UriComponentsBuilder.fromUriString(baseUrl)
                .path("/api/asset");
    }
}
