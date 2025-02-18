package org.apache.commons.lang3.builder;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import org.apache.commons.lang3.ArraySorter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

public class ReflectionDiffBuilder<T> implements Builder<DiffResult<T>> {

    /**
     * Constructs a new instance.
     *
     * @param <T> type of the left and right object.
     * @since 3.15.0
     */
    public static final class Builder<T> {

        private String[] excludeFieldNames = ArrayUtils.EMPTY_STRING_ARRAY;
        private DiffBuilder<T> diffBuilder;

        /**
         * Builds a new configured {@link ReflectionDiffBuilder}.
         *
         * @return a new configured {@link ReflectionDiffBuilder}.
         */
        public ReflectionDiffBuilder<T> build() {
            return new ReflectionDiffBuilder<>(diffBuilder, excludeFieldNames);
        }

        /**
         * Sets the DiffBuilder.
         *
         * @param diffBuilder the DiffBuilder.
         * @return this.
         */
        public Builder<T> setDiffBuilder(final DiffBuilder<T> diffBuilder) {
            this.diffBuilder = diffBuilder;
            return this;
        }

        /**
         * Sets field names to exclude from output. Intended for fields like {@code "password"} or {@code "lastModificationDate"}.
         *
         * @param excludeFieldNames field names to exclude.
         * @return this.
         */
        public Builder<T> setExcludeFieldNames(final String... excludeFieldNames) {
            this.excludeFieldNames = toExcludeFieldNames(excludeFieldNames);
            return this;
        }

    }

    /**
     * Constructs a new {@link Builder}.
     *
     * @param <T> type of the left and right object.
     * @return a new {@link Builder}.
     * @since 3.15.0
     */
    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    private static String[] toExcludeFieldNames(final String[] excludeFieldNames) {
        if (excludeFieldNames == null) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        // clone and remove nulls
        return ArraySorter.sort(ReflectionToStringBuilder.toNoNullStringArray(excludeFieldNames));
    }

    private final DiffBuilder<T> diffBuilder;

    /**
     * Field names to exclude from output. Intended for fields like {@code "password"} or {@code "lastModificationDate"}.
     */
    private String[] excludeFieldNames;

    private ReflectionDiffBuilder(final DiffBuilder<T> diffBuilder, final String[] excludeFieldNames) {
        this.diffBuilder = diffBuilder;
        this.excludeFieldNames = excludeFieldNames;
    }

    /**
     * Constructs a builder for the specified objects with the specified style.
     *
     * <p>
     * If {@code left == right} or {@code left.equals(right)} then the builder will not evaluate any calls to {@code append(...)} and will return an empty
     * {@link DiffResult} when {@link #build()} is executed.
     * </p>
     *
     * @param left  {@code this} object.
     * @param right the object to diff against.
     * @param style the style will use when outputting the objects, {@code null} uses the default
     * @throws IllegalArgumentException if {@code left} or {@code right} is {@code null}.
     * @deprecated Use {@link Builder}.
     */
    @Deprecated
    public ReflectionDiffBuilder(final T left, final T right, final ToStringStyle style) {
        this(DiffBuilder.<T>builder().setLeft(left).setRight(right).setStyle(style).build(), null);
    }

    private boolean accept(final Field field) {
        if (field.getName().indexOf(ClassUtils.INNER_CLASS_SEPARATOR_CHAR) != -1) {
            return false;
        }
        if (Modifier.isTransient(field.getModifiers())) {
            return false;
        }
        if (Modifier.isStatic(field.getModifiers())) {
            return false;
        }
        if (this.excludeFieldNames != null && Arrays.binarySearch(this.excludeFieldNames, field.getName()) >= 0) {
            // Reject fields from the getExcludeFieldNames list.
            return false;
        }
        return !field.isAnnotationPresent(DiffExclude.class);
    }

    private void appendFields(final Class<?> clazz) {
        for (final Field field : FieldUtils.getAllFields(clazz)) {
            if (accept(field)) {
                try {
                    diffBuilder.append(field.getName(), readField(field, getLeft()), readField(field, getRight()));
                } catch (final IllegalAccessException e) {
                    // this can't happen. Would get a Security exception instead
                    // throw a runtime exception in case the impossible happens.
                    throw new IllegalArgumentException("Unexpected IllegalAccessException: " + e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public DiffResult<T> build() {
        if (getLeft().equals(getRight())) {
            return diffBuilder.build();
        }

        appendFields(getLeft().getClass());
        return diffBuilder.build();
    }

    /**
     * Gets the field names that should be excluded from the diff.
     *
     * @return Returns the excludeFieldNames.
     * @since 3.13.0
     */
    public String[] getExcludeFieldNames() {
        return this.excludeFieldNames.clone();
    }

    private T getLeft() {
        return diffBuilder.getLeft();
    }

    private T getRight() {
        return diffBuilder.getRight();
    }

    private Object readField(final Field field, final Object target) throws IllegalAccessException {
        return FieldUtils.readField(field, target, true);
    }

    /**
     * Sets the field names to exclude.
     *
     * @param excludeFieldNames The field names to exclude from the diff or {@code null}.
     * @return {@code this}
     * @since 3.13.0
     * @deprecated Use {@link Builder#setExcludeFieldNames(String[])}.
     */
    @Deprecated
    public ReflectionDiffBuilder<T> setExcludeFieldNames(final String... excludeFieldNames) {
        this.excludeFieldNames = toExcludeFieldNames(excludeFieldNames);
        return this;
    }

}