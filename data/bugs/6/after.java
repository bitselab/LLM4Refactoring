package org.hibernate.dialect.function.array;

import java.util.List;
import java.util.function.Supplier;

import org.hibernate.metamodel.mapping.BasicValuedMapping;
import org.hibernate.metamodel.mapping.MappingModelExpressible;
import org.hibernate.metamodel.model.domain.DomainType;
import org.hibernate.query.ReturnableType;
import org.hibernate.query.sqm.produce.function.FunctionReturnTypeResolver;
import org.hibernate.query.sqm.tree.SqmTypedNode;
import org.hibernate.sql.ast.tree.SqlAstNode;
import org.hibernate.type.spi.TypeConfiguration;

/**
 * A {@link FunctionReturnTypeResolver} that resolves an array type based on the arguments,
 * which are supposed to be of the element type. The inferred type and implied type have precedence though.
 */
public class ArrayViaElementArgumentReturnTypeResolver implements FunctionReturnTypeResolver {

    public static final FunctionReturnTypeResolver INSTANCE = new ArrayViaElementArgumentReturnTypeResolver();

    private ArrayViaElementArgumentReturnTypeResolver() {
    }

    @Override
    public ReturnableType<?> resolveFunctionReturnType(
            ReturnableType<?> impliedType,
            Supplier<MappingModelExpressible<?>> inferredTypeSupplier,
            List<? extends SqmTypedNode<?>> arguments,
            TypeConfiguration typeConfiguration) {
        final MappingModelExpressible<?> inferredType = inferredTypeSupplier.get();
        if ( inferredType != null ) {
            if ( inferredType instanceof ReturnableType<?> ) {
                return (ReturnableType<?>) inferredType;
            }
            else if ( inferredType instanceof BasicValuedMapping ) {
                return (ReturnableType<?>) ( (BasicValuedMapping) inferredType ).getJdbcMapping();
            }
        }
        if ( impliedType != null ) {
            return impliedType;
        }
        for ( SqmTypedNode<?> argument : arguments ) {
            final DomainType<?> sqmType = argument.getExpressible().getSqmType();
            if ( sqmType instanceof ReturnableType<?> ) {
                return ArrayTypeResolver.resolveArrayType( sqmType, typeConfiguration );
            }
        }
        return null;
    }

    @Override
    public BasicValuedMapping resolveFunctionReturnType(
            Supplier<BasicValuedMapping> impliedTypeAccess,
            List<? extends SqlAstNode> arguments) {
        return null;
    }

}

import java.lang.reflect.Array;

import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.Size;
import org.hibernate.metamodel.mapping.JdbcMappingContainer;
import org.hibernate.metamodel.mapping.SqlTypedMapping;
import org.hibernate.metamodel.model.domain.DomainType;
import org.hibernate.sql.ast.SqlAstTranslator;
import org.hibernate.sql.ast.spi.AbstractSqlAstTranslator;
import org.hibernate.query.ReturnableType;
import org.hibernate.type.BasicPluralType;
import org.hibernate.type.BasicType;
import org.hibernate.type.descriptor.java.BasicPluralJavaType;
import org.hibernate.type.descriptor.sql.DdlType;
import org.hibernate.type.descriptor.sql.spi.DdlTypeRegistry;
import org.hibernate.type.spi.TypeConfiguration;

public class ArrayTypeResolver {

    public static ReturnableType<?> resolveArrayType(DomainType<?> elementType, TypeConfiguration typeConfiguration) {
        @SuppressWarnings("unchecked")
        BasicPluralJavaType<Object> arrayJavaType = (BasicPluralJavaType<Object>) typeConfiguration.getJavaTypeRegistry()
                .getDescriptor(
                        Array.newInstance(elementType.getBindableJavaType(), 0).getClass()
                );
        Dialect dialect = typeConfiguration.getCurrentBaseSqlTypeIndicators().getDialect();
        return arrayJavaType.resolveType(
                typeConfiguration,
                dialect,
                (BasicType<Object>) elementType,
                null,
                typeConfiguration.getCurrentBaseSqlTypeIndicators()
        );
    }
}