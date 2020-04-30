package io.rocketbase.commons.service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TestImageMagick {

    @Test
    @SneakyThrows
    public void testIdentity() {
        ProcessBuilder pb = new ProcessBuilder(
                "magick", "identify", "/Users/marten/Downloads/jpeg.webp");
        pb.redirectError();
        Process process = pb.start();
        String result = IOUtils.toString(process.getInputStream(), StandardCharsets.UTF_8.name());
        log.info("result: {}", result);
    }

    @Test
    @SneakyThrows
    public void testConvert() {
        File tempFile = File.createTempFile("asset-", ".jpg");

        ProcessBuilder pb = new ProcessBuilder(
                "magick", "/Users/marten/Downloads/jpeg.webp", tempFile.getAbsolutePath());
        pb.redirectError();
        Process process = pb.start();
        try {
            boolean result = process.waitFor(2L, TimeUnit.SECONDS);
            log.info("result: {}, {}", result, tempFile.getAbsolutePath());
        } catch (Exception e) {
            String error = IOUtils.toString(process.getErrorStream(), StandardCharsets.UTF_8.name());
            log.error("error... {}", error, e);

        }

    }
}
