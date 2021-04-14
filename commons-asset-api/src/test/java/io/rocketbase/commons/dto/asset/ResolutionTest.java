package io.rocketbase.commons.dto.asset;

import org.junit.Test;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;

public class ResolutionTest {

    @Test
    public void isLandscape() {
        // given
        Resolution resolutionOne = new Resolution(600,400);
        Resolution resolutionTwo = new Resolution(400,600);
        Resolution resolutionThree = new Resolution(600,600);
        // when
        boolean resultOne = resolutionOne.isLandscape();
        boolean resultTwo = resolutionTwo.isLandscape();
        boolean resultThree = resolutionThree.isLandscape();
        // then
        assertThat(resultOne, equalTo(true));
        assertThat(resultTwo, equalTo(false));
        assertThat(resultThree, equalTo(false));
    }

    @Test
    public void isBiggerThan() {
        // given
        Resolution resolutionOne = new Resolution(100,200);
        Resolution resolutionTwo = new Resolution(600,600);
        // when
        boolean resultOne = resolutionOne.isBiggerThan(resolutionTwo);
        boolean resultTwo = resolutionTwo.isBiggerThan(resolutionOne);
        boolean resultThree = resolutionOne.isBiggerThan(resolutionOne);
        // then
        assertThat(resultOne, equalTo(false));
        assertThat(resultTwo, equalTo(true));
        assertThat(resultThree, equalTo(false));
    }

    @Test
    public void shouldThumbBeCalculated() {
        // given
        Resolution resolution = new Resolution(PreviewSize.M.getMaxWidth(),PreviewSize.M.getMaxHeight());
        // when
        boolean resultOne = resolution.shouldThumbBeCalculated(PreviewSize.S);
        boolean resultTwo = resolution.shouldThumbBeCalculated(PreviewSize.M);
        boolean resultThree = resolution.shouldThumbBeCalculated(PreviewSize.L);
        // then
        assertThat(resultOne, equalTo(true));
        assertThat(resultTwo, equalTo(true));
        assertThat(resultThree, equalTo(false));
    }

    @Test
    public void testToString() {
        // given
        Resolution resolutionOne = new Resolution(600,400);
        Resolution resolutionTwo = new Resolution(2560,3840);
        // when
        String resultOne = resolutionOne.toString();
        String resultTwo = resolutionTwo.toString();
        // then
        assertThat(resultOne, equalTo("600 x 400"));
        assertThat(resultTwo, equalTo("2560 x 3840"));
    }
}