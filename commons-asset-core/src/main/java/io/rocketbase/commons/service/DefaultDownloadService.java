package io.rocketbase.commons.service;

import io.rocketbase.commons.dto.asset.AssetType;
import io.rocketbase.commons.exception.AssetErrorCodes;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class DefaultDownloadService implements DownloadService {


    private final Map<String, String> headerConfig;

    protected CloseableHttpClient httpClient = initHttpClient();

    protected CloseableHttpClient initHttpClient() {
        return HttpClientBuilder.create()
                .setConnectionTimeToLive(30, TimeUnit.SECONDS)
                .build();
    }

    public TempDownload downloadUrl(String url) {
        File tempFile = null;
        String filename = FilenameUtils.getName(url);
        AssetType type = null;

        try {
            HttpGet getRequest = new HttpGet(new URI(url));
            addHeaders(getRequest);
            HttpResponse response = httpClient.execute(getRequest);
            processResponse(response, filename, type, tempFile);
        } catch (IOException | URISyntaxException e) {
            throw new DownloadError(AssetErrorCodes.NOT_DOWNLOADABLE);
        }
        return new TempDownload(tempFile, filename, type);
    }

    protected void addHeaders(HttpGet getRequest) {
        if (headerConfig != null && !headerConfig.isEmpty()) {
            for (Map.Entry<String, String> header : headerConfig.entrySet()) {
                getRequest.addHeader(header.getKey(), header.getValue());
            }
        }
    }

    protected void processResponse(HttpResponse response, String filename, AssetType type, File tempFile) throws IOException {
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            String extractedFilename = extractFilename(response);
            if (extractedFilename != null) {
                filename = extractedFilename;
            }

            type = AssetType.findByFileExtension(FilenameUtils.getExtension(filename));
            if (type == null) {
                // store at first as jpeg later it will get checked by tika during store
                type = AssetType.JPEG;
            }
            tempFile = File.createTempFile("asset", type.getFileExtension());
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            entity.writeTo(outputStream);
            outputStream.close();
        } else {
            throw new DownloadError(AssetErrorCodes.NOT_DOWNLOADABLE);
        }
    }


}
