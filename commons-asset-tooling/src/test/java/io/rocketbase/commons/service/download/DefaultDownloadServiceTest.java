package io.rocketbase.commons.service.download;

import io.rocketbase.commons.dto.asset.AssetType;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mock.http.client.MockClientHttpResponse;

import java.io.IOException;
import java.net.URL;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class DefaultDownloadServiceTest {

    @Test
    public void extractFileNameWithCorrectFilename() throws IOException {
        // given
        DefaultDownloadService defaultDownloadService = new DefaultDownloadService();
        String filename = "12312321321.jpg";
        ClientHttpResponse response = buildResponse(filename);
        // when
        String result = defaultDownloadService.extractFilename(response);
        // then
        assertThat(result, notNullValue());
        assertThat(result, equalTo(filename));
    }

    @Test
    public void processDownload() throws IOException {
        // given
        DefaultDownloadService defaultDownloadService = new DefaultDownloadService();
        String filename = "12312321321.jpg";
        ClientHttpResponse response = buildResponse(filename);
        // when
        DownloadService.TempDownload result = defaultDownloadService.processResponse(response, filename);
        // then
        assertThat(result, notNullValue());
        assertThat(result.getFilename(), equalTo(filename));
        assertThat(result.getType(), equalTo(AssetType.JPEG));
        assertThat(result.getFile(), notNullValue());
    }

    @Test
    public void processDownloadWithoutFilename() throws IOException {
        // given
        DefaultDownloadService defaultDownloadService = new DefaultDownloadService();
        ClientHttpResponse response = buildResponse(null);
        // when
        DownloadService.TempDownload result = defaultDownloadService.processResponse(response, null);
        // then
        assertThat(result, notNullValue());
        assertThat(result.getFilename(), nullValue());
        assertThat(result.getType(), nullValue());
        assertThat(result.getFile(), notNullValue());
    }

    private ClientHttpResponse buildResponse(String filename) throws IOException {
        URL asset = ClassLoader.getSystemResource("assets/rocketbase.gif");

        ClientHttpResponse response = new MockClientHttpResponse(asset.openStream(), HttpStatus.OK);
        if (filename != null) {
            response.getHeaders().add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + filename);
        }
        return response;
    }
}