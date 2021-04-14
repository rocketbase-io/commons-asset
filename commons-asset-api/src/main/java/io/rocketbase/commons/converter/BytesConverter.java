package io.rocketbase.commons.converter;

import org.springframework.context.i18n.LocaleContextHolder;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

public final class BytesConverter {

    public static String humanReadableBytes(long bytes) {
        long absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
        if (absB < 1024) {
            return bytes + " B";
        }
        long value = absB;
        CharacterIterator ci = new StringCharacterIterator("KMGTPE");
        for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
            value >>= 10;
            ci.next();
        }
        value *= Long.signum(bytes);
        return String.format(LocaleContextHolder.getLocale(), "%.1f %cb", value / 1024.0, ci.current());
    }
}
