package io.rocketbase.commons.service;

import io.rocketbase.commons.dto.asset.AssetType;
import io.rocketbase.commons.exception.AssetErrorCodes;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import javax.net.ssl.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.Map;

@Slf4j
public class DefaultDownloadService implements DownloadService {

    private final Headers headers;

    protected OkHttpClient httpClient;

    public DefaultDownloadService() {
        this.headers = new Headers.Builder().build();
    }

    public DefaultDownloadService(Map<String, String> config) {
        this.headers = convertHeaders(config);
    }

    @SneakyThrows
    protected OkHttpClient getHttpClient() {
        if (httpClient == null) {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            // builder.callTimeout(Duration.ofSeconds(30));

            httpClient = builder.build();
        }
        return httpClient;
    }

    public TempDownload downloadUrl(String url) {
        String filename = FilenameUtils.getName(url);

        try {
            Request request = new Request.Builder()
                    .headers(headers)
                    .url(url)
                    .get()
                    .build();
            Response response = getHttpClient().newCall(request).execute();
            return processResponse(response, filename);
        } catch (IOException | IllegalStateException e) {
            throw new DownloadError(AssetErrorCodes.NOT_DOWNLOADABLE);
        }
    }

    protected Headers convertHeaders(Map<String, String> config) {
        Headers.Builder builder = new Headers.Builder();
        if (config != null && !config.isEmpty()) {
            for (Map.Entry<String, String> header : config.entrySet()) {
                builder.add(header.getKey(), header.getValue());
            }
        }
        return builder.build();
    }

    protected TempDownload processResponse(Response response, String filename) throws IOException {
        if (response != null) {
            String extractedFilename = extractFilename(response);
            if (extractedFilename != null && extractedFilename.contains(".")) {
                filename = extractedFilename;
            }
            AssetType type = AssetType.findByFileExtension(FilenameUtils.getExtension(filename));
            File tempFile = File.createTempFile("asset-", type != null ? type.getFileExtensionForSuffix() : ".tmp");
            @Cleanup FileOutputStream outputStream = new FileOutputStream(tempFile);
            IOUtils.copy(response.body().byteStream(), outputStream);
            return new TempDownload(tempFile, filename, type);
        } else {
            throw new DownloadError(AssetErrorCodes.NOT_DOWNLOADABLE);
        }
    }


}
