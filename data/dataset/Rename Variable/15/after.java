package org.mockito.junit.jupiter;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.create;
import static org.junit.platform.commons.support.AnnotationSupport.findAnnotation;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.mockito.ScopedMock;
import org.mockito.internal.configuration.plugins.Plugins;
import org.mockito.internal.session.MockitoSessionLoggerAdapter;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.resolver.CaptorParameterResolver;
import org.mockito.junit.jupiter.resolver.CompositeParameterResolver;
import org.mockito.junit.jupiter.resolver.MockParameterResolver;
import org.mockito.quality.Strictness;

public class MockitoExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

    private final static Namespace MOCKITO = create("org.mockito");

    private final static String SESSION = "session", MOCKS = "mocks";

    private final Strictness strictness;

    private final ParameterResolver parameterResolver;

    // This constructor is invoked by JUnit Jupiter via reflection or ServiceLoader
    @SuppressWarnings("unused")
    public MockitoExtension() {
        this(Strictness.STRICT_STUBS);
    }

    private MockitoExtension(Strictness strictness) {
        this.strictness = strictness;
        this.parameterResolver = new CompositeParameterResolver(
                new MockParameterResolver(),
                new CaptorParameterResolver()
        );
    }

    /**
     * Callback that is invoked <em>before</em> each test is invoked.
     *
     * @param context the current extension context; never {@code null}
     */
    @Override
    public void beforeEach(final ExtensionContext context) {
        List<Object> testInstances = context.getRequiredTestInstances().getAllInstances();

        Strictness actualStrictness = this.retrieveAnnotationFromTestClasses(context)
                .map(MockitoSettings::strictness)
                .orElse(strictness);

        MockitoSession session = Mockito.mockitoSession()
                .initMocks(testInstances.toArray())
                .strictness(actualStrictness)
                .logger(new MockitoSessionLoggerAdapter(Plugins.getMockitoLogger()))
                .startMocking();

        context.getStore(MOCKITO).put(MOCKS, new HashSet<>());
        context.getStore(MOCKITO).put(SESSION, session);
    }

    private Optional<MockitoSettings> retrieveAnnotationFromTestClasses(final ExtensionContext context) {
        ExtensionContext currentContext = context;
        Optional<MockitoSettings> annotation;

        do {
            annotation = findAnnotation(currentContext.getElement(), MockitoSettings.class);

            if (currentContext.getParent().isEmpty()) {
                break;
            }

            currentContext = currentContext.getParent().get();
        } while (annotation.isEmpty() && currentContext != context.getRoot());

        return annotation;
    }

    /**
     * Callback that is invoked <em>after</em> each test has been invoked.
     *
     * @param context the current extension context; never {@code null}
     */
    @Override
    @SuppressWarnings("unchecked")
    public void afterEach(ExtensionContext context) {
        context.getStore(MOCKITO).remove(MOCKS, Set.class).forEach(mock -> ((ScopedMock) mock).closeOnDemand());
        context.getStore(MOCKITO).remove(SESSION, MockitoSession.class)
                .finishMocking(context.getExecutionException().orElse(null));
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext context) throws ParameterResolutionException {
        return parameterResolver.supportsParameter(parameterContext, context);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext context) throws ParameterResolutionException {
        Object resolvedParameter = parameterResolver.resolveParameter(parameterContext, context);
        if (resolvedParameter instanceof ScopedMock) {
            context.getStore(MOCKITO).get(MOCKS, Set.class).add(resolvedParameter);
        }
        return resolvedParameter;
    }
}