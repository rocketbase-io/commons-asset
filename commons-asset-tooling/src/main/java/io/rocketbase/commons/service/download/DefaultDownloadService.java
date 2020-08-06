package io.rocketbase.commons.service.download;

import io.rocketbase.commons.dto.asset.AssetType;
import io.rocketbase.commons.exception.AssetErrorCodes;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.Map;

@Slf4j
public class DefaultDownloadService implements DownloadService {

    private final HttpHeaders headers;

    protected RestTemplate restTemplate;

    public DefaultDownloadService() {
        this.headers = new HttpHeaders();
    }

    public DefaultDownloadService(Map<String, String> config) {
        this.headers = convertHeaders(config);
    }

    @SneakyThrows
    protected RestTemplate getRestTemplate() {
        if (restTemplate == null) {
            TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

            SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
                    .loadTrustMaterial(null, acceptingTrustStrategy)
                    .build();

            SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

            CloseableHttpClient httpClient = HttpClients.custom()
                    .setSSLSocketFactory(csf)
                    .build();

            HttpComponentsClientHttpRequestFactory requestFactory =
                    new HttpComponentsClientHttpRequestFactory();

            requestFactory.setHttpClient(httpClient);

            restTemplate = new RestTemplate(requestFactory);
            // add header interceptor
            restTemplate.getInterceptors().add((request, body, execution) -> {
                request.getHeaders().addAll(headers);
                return execution.execute(request, body);
            });

        }
        return restTemplate;
    }

    public TempDownload downloadUrl(String url) {
        String filename = FilenameUtils.getName(url);
        try {
            TempDownload tempDownload = getRestTemplate().execute(url, HttpMethod.GET, null,
                    new ResponseExtractor<TempDownload>() {
                        @Override
                        public TempDownload extractData(ClientHttpResponse response) throws IOException {
                            return processResponse(response, filename);
                        }
                    });
            return tempDownload;
        } catch (Exception e) {
            throw new DownloadError(AssetErrorCodes.NOT_DOWNLOADABLE);
        }
    }

    protected HttpHeaders convertHeaders(Map<String, String> config) {
        HttpHeaders headers = new HttpHeaders();
        if (config != null && !config.isEmpty()) {
            for (Map.Entry<String, String> header : config.entrySet()) {
                headers.add(header.getKey(), header.getValue());
            }
        }
        return headers;
    }

    protected TempDownload processResponse(ClientHttpResponse response, String filename) throws IOException {
        if (response != null) {
            String extractedFilename = extractFilename(response);
            if (extractedFilename != null && extractedFilename.contains(".")) {
                filename = extractedFilename;
            }
            AssetType type = AssetType.findByFileExtension(FilenameUtils.getExtension(filename));
            File tempFile = File.createTempFile("asset-", type != null ? type.getFileExtensionForSuffix() : ".tmp");
            @Cleanup FileOutputStream outputStream = new FileOutputStream(tempFile);
            IOUtils.copy(response.getBody(), outputStream);
            return new TempDownload(tempFile, filename, type);
        } else {
            throw new DownloadError(AssetErrorCodes.NOT_DOWNLOADABLE);
        }
    }


}
