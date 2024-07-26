package org.junit.jupiter.params.converter;

class StringToEnumConverter implements StringToObjectConverter {

    @Override
    public boolean canConvert(Class<?> targetType) {
        return targetType.isEnum();
    }

    @Override
    public Object convert(String source, Class<?> targetType) throws Exception {
        return Enum.valueOf((Class<Enum>) targetType, source);
    }

}