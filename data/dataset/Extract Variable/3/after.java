package org.apache.commons.lang3.time;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import org.apache.commons.lang3.AbstractLangTest;
import org.apache.commons.lang3.ArraySorter;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junitpioneer.jupiter.DefaultLocale;
import org.junitpioneer.jupiter.DefaultTimeZone;
import org.junitpioneer.jupiter.ReadsDefaultLocale;
import org.junitpioneer.jupiter.ReadsDefaultTimeZone;

/* make test reproducible */ @DefaultLocale(language = "en")
/* make test reproducible */ @DefaultTimeZone(TimeZones.GMT_ID)
/* make test reproducible */ @ReadsDefaultLocale
/* make test reproducible */ @ReadsDefaultTimeZone
public class FastDateParser_TimeZoneStrategyTest extends AbstractLangTest {

    private static final String[] FAILS_ON_GH_JAVA_17 = { "zh_HK", "zh_HK_#Hans", "zh_MO_#Hans", "nn", "nn_NO_#Latn", "nn_NO", "pt_AO", "pt_CV", "pt_GW",
            "pt_LU", "pt_PT", "pt_TL", "es_AR", "es_BZ", "es_BR", "es_CL", "es_CO", "es_CR", "es_CU", "es_EC", "es_SV", "es_GT", "es_HN", "es_419", "es_MX",
            "es_NI", "es_PA", "es_PY", "es_PE", "es_PR", "es_UY", "es_VE", "sv_SE_#Latn", "sv_SE" };

    private static final List<Locale> Java17Failures = new ArrayList<>();

    static {
        Arrays.sort(FAILS_ON_GH_JAVA_17);
    }

    @AfterAll
    public static void afterAll() {
        if (!Java17Failures.isEmpty()) {
            System.err.printf("Expected failures on Java 17: %,d%n%s%n", FAILS_ON_GH_JAVA_17.length, Arrays.toString(FAILS_ON_GH_JAVA_17));
            System.err.printf("Actual failures on Java 17: %,d%n%s%n", Java17Failures.size(), Java17Failures);
        }
    }

    public static Locale[] getAvailableLocalesSorted() {
        return ArraySorter.sort(Locale.getAvailableLocales(), Comparator.comparing(Locale::getDisplayName));
    }

    @Test
    public void testLang1219() throws ParseException {
        final FastDateParser parser = new FastDateParser("dd.MM.yyyy HH:mm:ss z", TimeZone.getDefault(), Locale.GERMAN);
        final Date summer = parser.parse("26.10.2014 02:00:00 MESZ");
        final Date standard = parser.parse("26.10.2014 02:00:00 MEZ");
        assertNotEquals(summer.getTime(), standard.getTime());
    }

    @ParameterizedTest
    @MethodSource("org.apache.commons.lang3.time.FastDateParser_TimeZoneStrategyTest#getAvailableLocalesSorted")
    public void testTimeZoneStrategy_DateFormatSymbols(final Locale locale) {
        testTimeZoneStrategyPattern_DateFormatSymbols_getZoneStrings(locale);
    }

    @ParameterizedTest
    @MethodSource("org.apache.commons.lang3.time.FastDateParser_TimeZoneStrategyTest#getAvailableLocalesSorted")
    public void testTimeZoneStrategy_TimeZone(final Locale locale) {
        testTimeZoneStrategyPattern_TimeZone_getAvailableIDs(locale);
    }

    private void testTimeZoneStrategyPattern(final String languageTag, final String source) throws ParseException {
        final Locale locale = Locale.forLanguageTag(languageTag);
        assumeFalse(LocaleUtils.isLanguageUndetermined(locale), () -> toFailureMessage(locale, languageTag));
        assumeTrue(LocaleUtils.isAvailableLocale(locale), () -> toFailureMessage(locale, languageTag));
        final TimeZone tzDefault = TimeZone.getTimeZone("Etc/UTC");
        final FastDateParser parser = new FastDateParser("z", tzDefault, locale);
        parser.parse(source);
        testTimeZoneStrategyPattern_TimeZone_getAvailableIDs(locale);
    }

    private void testTimeZoneStrategyPattern_DateFormatSymbols_getZoneStrings(final Locale locale) {
        Objects.requireNonNull(locale, "locale");
        assumeFalse(LocaleUtils.isLanguageUndetermined(locale), () -> toFailureMessage(locale, null));
        assumeTrue(LocaleUtils.isAvailableLocale(locale), () -> toFailureMessage(locale, null));

        final String[][] zones = ArraySorter.sort(DateFormatSymbols.getInstance(locale).getZoneStrings(), Comparator.comparing(array -> array[0]));
        for (final String[] zone : zones) {
            for (int zIndex = 1; zIndex < zone.length; ++zIndex) {
                final String tzDisplay = zone[zIndex];
                if (tzDisplay == null) {
                    break;
                }
                final TimeZone timeZone = TimeZone.getDefault();
                final FastDateParser parser = new FastDateParser("z", timeZone, locale);
                // An exception will be thrown and the test will fail if parsing isn't successful
                try {
                    parser.parse(tzDisplay);
                } catch (ParseException e) {
                    // How do I know I'm on GH?
                    final String localeStr = locale.toString();
                    if (SystemUtils.IS_JAVA_17 && Arrays.binarySearch(FAILS_ON_GH_JAVA_17, localeStr) > 0) {
                        Java17Failures.add(locale);
                        // Mark as an assumption failure instead of a hard fail
                        System.err.println("Java 17 - Mark as an assumption failure instead of a hard fail: " + localeStr);
                        assumeTrue(false, localeStr);
                        continue;
                    }
                    final String msg = String.format("%s: with locale = %s, zIndex = %,d, tzDisplay = '%s', parser = '%s'", e, locale, zIndex, tzDisplay,
                            parser.toString());
                    fail(msg, e);
                }
            }
        }
    }

    /**
     * Breaks randomly on GitHub for Locale "pt_PT", TimeZone "Etc/UTC" if we do not check if the Locale's language is "undetermined".
     *
     * @throws ParseException
     */
    private void testTimeZoneStrategyPattern_TimeZone_getAvailableIDs(final Locale locale) {
        Objects.requireNonNull(locale, "locale");
        assumeFalse(LocaleUtils.isLanguageUndetermined(locale), () -> toFailureMessage(locale, null));
        assumeTrue(LocaleUtils.isAvailableLocale(locale), () -> toFailureMessage(locale, null));

        for (final String id : ArraySorter.sort(TimeZone.getAvailableIDs())) {
            final TimeZone timeZone = TimeZone.getTimeZone(id);
            final FastDateParser parser = new FastDateParser("z", timeZone, locale);
            final String displayName = timeZone.getDisplayName(locale);
            try {
                parser.parse(displayName);
            } catch (ParseException e) {
                // Missing "Zulu" or something else in broken JDK's GH builds?
                fail(String.format("%s: with locale = %s, id = '%s', timeZone = %s, displayName = '%s', parser = '%s'", e, locale, id, timeZone, displayName,
                        parser.toStringAll()), e);
            }
        }
    }

    /**
     * Breaks randomly on GitHub for Locale "pt_PT", TimeZone "Etc/UTC" if we do not check if the Locale's language is "undetermined".
     *
     * <pre>{@code
     * java.text.ParseException: Unparseable date: Horário do Meridiano de Greenwich: with tzDefault =
     * sun.util.calendar.ZoneInfo[id="Etc/UTC",offset=0,dstSavings=0,useDaylight=false,transitions=0,lastRule=null], locale = pt_LU, zones[][] size = '601',
     * zone[] size = '7', zIndex = 3, tzDisplay = 'Horário do Meridiano de Greenwich'
     * }</pre>
     *
     * @throws ParseException Test failure
     */
    @Test
    public void testTimeZoneStrategyPatternPortugal() throws ParseException {
        testTimeZoneStrategyPattern("pt_PT", "Horário do Meridiano de Greenwich");
    }

    /**
     * Breaks randomly on GitHub for Locale "sr_ME_#Cyrl", TimeZone "Etc/UTC" if we do not check if the Locale's language is "undetermined".
     *
     * <pre>{@code
     * java.text.ParseException: Unparseable date: Srednje vreme po Griniču: with tzDefault = sun.util.calendar.ZoneInfo[id="Etc/UTC",
     * offset=0,dstSavings=0,useDaylight=false,transitions=0,lastRule=null], locale = sr_ME_#Cyrl, zones[][] size = '601',
     * zone[] size = '7', zIndex = 3, tzDisplay = 'Srednje vreme po Griniču'
     * }</pre>
     *
     * @throws ParseException Test failure
     */
    @Test
    public void testTimeZoneStrategyPatternSuriname() throws ParseException {
        testTimeZoneStrategyPattern("sr_ME_#Cyrl", "Srednje vreme po Griniču");
    }

    private String toFailureMessage(final Locale locale, final String languageTag) {
        return String.format("locale = %s, languageTag = '%s', isAvailableLocale = %s, isLanguageUndetermined = %s", languageTag, locale,
                LocaleUtils.isAvailableLocale(locale), LocaleUtils.isLanguageUndetermined(locale));
    }
}