package io.rocketbase.commons.service;

import com.google.common.io.BaseEncoding;
import io.rocketbase.commons.dto.asset.AssetType;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.springframework.http.HttpHeaders;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class DefaultDownloadServiceTest {

    @Test
    public void extractFileNameWithCorrectFilename() throws IOException {
        // given
        DefaultDownloadService defaultDownloadService = new DefaultDownloadService();
        String filename = "12312321321.jpg";
        Response response = buildResponse(filename);
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
        Response response = buildResponse(filename);
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
        Response response = buildResponse(null);
        // when
        DownloadService.TempDownload result = defaultDownloadService.processResponse(response, null);
        // then
        assertThat(result, notNullValue());
        assertThat(result.getFilename(), nullValue());
        assertThat(result.getType(), nullValue());
        assertThat(result.getFile(), notNullValue());
    }

    @NotNull
    private Response buildResponse(String filename) {
        Response.Builder builder = new Response.Builder()
                .request(new Request.Builder().url("http://localhost").build())
                .protocol(Protocol.HTTP_2)
                .code(200)
                .message("")
                .body(ResponseBody.create(BaseEncoding.base64().decode("R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw=="), MediaType.get("image/gif")));
        if (filename != null) {
            builder.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + filename);
        }
        return builder.build();
    }
}