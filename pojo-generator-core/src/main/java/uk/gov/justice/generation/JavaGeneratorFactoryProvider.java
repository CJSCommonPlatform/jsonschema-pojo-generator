package uk.gov.justice.generation;

import uk.gov.justice.generation.pojo.core.GenerationContext;
import uk.gov.justice.generation.pojo.generators.JavaGeneratorFactory;
import uk.gov.justice.generation.pojo.plugin.PluginProvider;

public class JavaGeneratorFactoryProvider {

    private final ClassNameFactoryProvider classNameFactoryProvider;

    public JavaGeneratorFactoryProvider(final ClassNameFactoryProvider classNameFactoryProvider) {
        this.classNameFactoryProvider = classNameFactoryProvider;
    }

    public JavaGeneratorFactory create(final GenerationContext generationContext, final PluginProvider pluginProvider) {
        return new JavaGeneratorFactory(classNameFactoryProvider.create(generationContext, pluginProvider));
    }
}
