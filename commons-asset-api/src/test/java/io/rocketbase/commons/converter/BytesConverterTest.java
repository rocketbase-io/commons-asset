package io.rocketbase.commons.converter;

import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class BytesConverterTest {

    @Test
    public void humanReadableBytes() {
        // given
        // when
        LocaleContextHolder.setLocale(Locale.GERMAN);
        String resultOne = BytesConverter.humanReadableBytes(1023);
        String resultTwo = BytesConverter.humanReadableBytes(1024);
        String resultThree = BytesConverter.humanReadableBytes(1048576);
        // then
        assertThat(resultOne, equalTo("1023 B"));
        assertThat(resultTwo, equalTo("1,0 Kb"));
        assertThat(resultThree, equalTo("1,0 Mb"));
    }
}