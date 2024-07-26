package com.github.javaparser.resolution.types.parametrization;

import com.github.javaparser.resolution.declarations.ResolvedTypeParameterDeclaration;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.resolution.types.ResolvedWildcard;

import java.util.Optional;

/**
 * @author Federico Tomassetti
 */
public interface ResolvedTypeParameterValueProvider {

    /**
     * Calculate the value for the given type parameter.
     * It could be inherited.
     */
    Optional<ResolvedType> typeParamValue(ResolvedTypeParameterDeclaration typeParameterDeclaration);

    /**
     * Replace the type typeParametersValues present in the given type with the ones for which this type
     * has a value.
     */
    default ResolvedType useThisTypeParametersOnTheGivenType(ResolvedType type) {
        if (type.isTypeVariable()) {
            ResolvedTypeParameterDeclaration typeParameter = type.asTypeParameter();
            if (typeParameter.declaredOnType()) {
                Optional<ResolvedType> typeParam = typeParamValue(typeParameter);
                if (typeParam.isPresent()) {
                    ResolvedType resolvedTypeParam = typeParam.get();
                    // Try to avoid an infinite loop when the type is a wildcard type bounded by a type variable like "? super T" 
                    if (resolvedTypeParam.isWildcard() &&
                            ( !resolvedTypeParam.asWildcard().equals(ResolvedWildcard.UNBOUNDED)
                                    && type.equals(resolvedTypeParam.asWildcard().getBoundedType()))) {
                        return type;
                    }
                    type = resolvedTypeParam;
                }
            }
        }
        if (type.isWildcard() && type.asWildcard().isBounded()) {
            if (type.asWildcard().isExtends()) {
                return ResolvedWildcard.extendsBound(useThisTypeParametersOnTheGivenType(type.asWildcard().getBoundedType()));
            }
            return ResolvedWildcard.superBound(useThisTypeParametersOnTheGivenType(type.asWildcard().getBoundedType()));
        }
        if (type.isReferenceType()) {
            type = type.asReferenceType().transformTypeParameters(this::useThisTypeParametersOnTheGivenType);
        }
        return type;
    }

    Optional<ResolvedType> getGenericParameterByName(String name);
}