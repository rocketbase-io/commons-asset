package io.rocketbase.commons.dto.asset;

import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class PreviewSizeTest {

    @Test
    public void getSmallerAs() {
        // given
        PreviewSize smallerAs = PreviewSize.M;
        // when
        List<PreviewSize> result = PreviewSize.getSmallerAs(smallerAs);
        // then
        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(2));
        assertThat(result, hasItems(PreviewSize.S, PreviewSize.XS));
    }

    @Test
    public void getByName() {
        // given
        String name = "s";
        // when
        PreviewSize result = PreviewSize.getByName(name, PreviewSize.M);
        // then
        assertThat(result, notNullValue());
        assertThat(result, equalTo(PreviewSize.S));
    }
}