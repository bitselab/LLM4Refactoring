package org.junit.jupiter.params.converter;

class StringToEnumConverter implements StringToObjectConverter {

    @Override
    public boolean canConvert(Class<?> targetType) {
        return targetType.isEnum();
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Object convert(String source, Class targetType) throws Exception {
        return Enum.valueOf(targetType, source);
    }

}