package org.apache.commons.lang3.time;

import org.apache.commons.lang3.time.FastDateParser_TimeZoneStrategyTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Locale;

import static org.mockito.Mockito.*;

public class FastDateParser_TimeZoneStrategyTestTest {
    @Mock
    List<Locale> Java17Failures;
    @InjectMocks
    FastDateParser_TimeZoneStrategyTest fastDateParser_TimeZoneStrategyTest;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAfterAll() throws Exception {
        when(Java17Failures.size()).thenReturn(0);
        when(Java17Failures.isEmpty()).thenReturn(true);

        FastDateParser_TimeZoneStrategyTest.afterAll();
    }

    @Test
    public void testGetAvailableLocalesSorted() throws Exception {
        Locale[] result = FastDateParser_TimeZoneStrategyTest.getAvailableLocalesSorted();
        Assert.assertArrayEquals(new Locale[]{new Locale("language", "country", "variant")}, result);
    }

    @Test
    public void testTestLang1219() throws Exception {
        fastDateParser_TimeZoneStrategyTest.testLang1219();
    }

    @Test
    public void testTestTimeZoneStrategy_DateFormatSymbols() throws Exception {
        when(Java17Failures.add(any(Locale.class))).thenReturn(true);

        fastDateParser_TimeZoneStrategyTest.testTimeZoneStrategy_DateFormatSymbols(new Locale("language", "country", "variant"));
    }

    @Test
    public void testTestTimeZoneStrategy_TimeZone() throws Exception {
        fastDateParser_TimeZoneStrategyTest.testTimeZoneStrategy_TimeZone(new Locale("language", "country", "variant"));
    }

    @Test
    public void testTestTimeZoneStrategyPatternPortugal() throws Exception {
        fastDateParser_TimeZoneStrategyTest.testTimeZoneStrategyPatternPortugal();
    }

    @Test
    public void testTestTimeZoneStrategyPatternSuriname() throws Exception {
        fastDateParser_TimeZoneStrategyTest.testTimeZoneStrategyPatternSuriname();
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme