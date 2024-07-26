package org.apache.commons.lang3.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.AbstractLangTest;
import org.junit.jupiter.api.Test;

/**
 * Unit tests {@link DiffResult}.
 */
public class DiffResultTest extends AbstractLangTest {

    private static final class EmptyClass {
        // empty
    }

    private static final class SimpleClass implements Diffable<SimpleClass> {
        static String getFieldName() {
            return "booleanField";
        }

        private final boolean booleanField;

        SimpleClass(final boolean booleanField) {
            this.booleanField = booleanField;
        }

        @Override
        public DiffResult<SimpleClass> diff(final SimpleClass obj) {
            return new DiffBuilder<>(this, obj, ToStringStyle.SHORT_PREFIX_STYLE).append(getFieldName(), booleanField, obj.booleanField).build();
        }
    }

    private static final ToStringStyle SHORT_STYLE = ToStringStyle.SHORT_PREFIX_STYLE;

    private static final SimpleClass SIMPLE_FALSE = new SimpleClass(false);

    private static final SimpleClass SIMPLE_TRUE = new SimpleClass(true);

    @Test
    public void testDefaultStyle() {
        final DiffResult<SimpleClass> diffResult = new DiffResult<>(SIMPLE_TRUE, SIMPLE_FALSE, SIMPLE_TRUE.diff(SIMPLE_FALSE).getDiffs(),
                ToStringStyle.DEFAULT_STYLE, DiffBuilder.TO_STRING_FORMAT);
        assertEquals(ToStringStyle.DEFAULT_STYLE, diffResult.getToStringStyle());
    }

    @Test
    public void testIterator() {
        final SimpleClass lhs = new SimpleClass(true);
        final SimpleClass rhs = new SimpleClass(false);

        final List<Diff<?>> diffs = lhs.diff(rhs).getDiffs();
        final Iterator<Diff<?>> expectedIterator = diffs.iterator();

        final DiffResult<SimpleClass> list = new DiffResult<>(lhs, rhs, diffs, SHORT_STYLE, DiffBuilder.TO_STRING_FORMAT);
        final Iterator<Diff<?>> iterator = list.iterator();

        while (iterator.hasNext()) {
            assertTrue(expectedIterator.hasNext());
            assertEquals(expectedIterator.next(), iterator.next());
        }
    }

    @Test
    public void testLeftAndRightGetters() {
        final SimpleClass left = new SimpleClass(true);
        final SimpleClass right = new SimpleClass(false);

        final List<Diff<?>> diffs = left.diff(right).getDiffs();
        final DiffResult diffResult = new DiffResult(left, right, diffs, SHORT_STYLE, DiffBuilder.TO_STRING_FORMAT);

        assertEquals(left, diffResult.getLeft());
        assertEquals(right, diffResult.getRight());
    }

    @Test
    public void testListIsNonModifiable() {
        final SimpleClass lhs = new SimpleClass(true);
        final SimpleClass rhs = new SimpleClass(false);

        final List<Diff<?>> diffs = lhs.diff(rhs).getDiffs();

        final DiffResult<SimpleClass> result = new DiffResult<>(lhs, rhs, diffs, SHORT_STYLE, DiffBuilder.TO_STRING_FORMAT);
        assertEquals(diffs, result.getDiffs());
        assertEquals(1, result.getNumberOfDiffs());
        assertThrows(UnsupportedOperationException.class, () -> result.getDiffs().remove(0));
    }

    @Test
    public void testNoDifferencesString() {
        final DiffResult<SimpleClass> diffResult = DiffBuilder.<SimpleClass>builder().setLeft(SIMPLE_TRUE).setRight(SIMPLE_TRUE).setStyle(SHORT_STYLE).build()
                .build();
        assertEquals(DiffResult.OBJECTS_SAME_STRING, diffResult.toString());
    }

    @Test
    public void testNullLhs() {
        assertThrows(NullPointerException.class,
                () -> new DiffResult<>(null, SIMPLE_FALSE, SIMPLE_TRUE.diff(SIMPLE_FALSE).getDiffs(), SHORT_STYLE, DiffBuilder.TO_STRING_FORMAT));
    }

    @Test
    public void testNullList() {
        assertThrows(NullPointerException.class, () -> new DiffResult<>(SIMPLE_TRUE, SIMPLE_FALSE, null, SHORT_STYLE, null));
    }

    @Test
    public void testNullRhs() {
        assertThrows(NullPointerException.class,
                () -> new DiffResult<>(SIMPLE_TRUE, null, SIMPLE_TRUE.diff(SIMPLE_FALSE).getDiffs(), SHORT_STYLE, DiffBuilder.TO_STRING_FORMAT));
    }

    @Test
    public void testToStringOutput() {
        // @formatter:off
        final DiffResult<EmptyClass> result = DiffBuilder.<EmptyClass>builder()
                .setLeft(new EmptyClass())
                .setRight(new EmptyClass())
                .setStyle(ToStringStyle.SHORT_PREFIX_STYLE)
                .build()
                .append("test", false, true)
                .build();
        // @formatter:on
        assertEquals("DiffResultTest.EmptyClass[test=false] differs from DiffResultTest.EmptyClass[test=true]", result.toString());
    }

    @Test
    public void testToStringFormat() {
        // @formatter:off
        final DiffResult<EmptyClass> result = DiffBuilder.<EmptyClass>builder()
                .setLeft(new EmptyClass())
                .setRight(new EmptyClass())
                .setStyle(ToStringStyle.SHORT_PREFIX_STYLE)
                .setToStringFormat("%s <> %s")
                .build()
                .append("test", false, true)
                .build();
        // @formatter:on
        assertEquals("DiffResultTest.EmptyClass[test=false] <> DiffResultTest.EmptyClass[test=true]", result.toString());
    }

    @Test
    public void testToStringSpecifyStyleOutput() {
        final DiffResult<SimpleClass> result = SIMPLE_FALSE.diff(SIMPLE_TRUE);
        assertEquals(result.getToStringStyle(), SHORT_STYLE);

        // @formatter:off
        final String lhsString = new ToStringBuilder(SIMPLE_FALSE, ToStringStyle.MULTI_LINE_STYLE)
                .append(SimpleClass.getFieldName(), SIMPLE_FALSE.booleanField)
                .build();

        final String rhsString = new ToStringBuilder(SIMPLE_TRUE, ToStringStyle.MULTI_LINE_STYLE)
                .append(SimpleClass.getFieldName(), SIMPLE_TRUE.booleanField)
                .build();
        // @formatter:on

        assertEquals(String.format("%s differs from %s", lhsString, rhsString), result.toString(ToStringStyle.MULTI_LINE_STYLE));
    }
}