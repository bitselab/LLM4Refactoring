package org.junit.jupiter.params.converter;

class StringToEnumConverter implements StringToObjectConverter {

    @Override
    public boolean canConvert(Class<?> targetType) {
        return targetType.isEnum();
    }

    @Override
    public Object convert(String source, Class<?> targetType) throws Exception {
        return valueOf(targetType, source);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static Object valueOf(Class targetType, String source) {
        return Enum.valueOf(targetType, source);
    }

}