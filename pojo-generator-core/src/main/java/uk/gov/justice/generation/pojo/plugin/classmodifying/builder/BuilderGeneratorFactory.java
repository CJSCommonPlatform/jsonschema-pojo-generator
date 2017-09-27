package uk.gov.justice.generation.pojo.plugin.classmodifying.builder;

import uk.gov.justice.generation.pojo.dom.ClassDefinition;
import uk.gov.justice.generation.pojo.generators.JavaGeneratorFactory;
import uk.gov.justice.generation.pojo.plugin.classmodifying.PluginContext;

/**
 * Factory for creating a {@link BuilderGenerator}
 */
public class BuilderGeneratorFactory {

    /**
     * Creates a {@link BuilderGenerator}
     *
     * @param classDefinition      The {@link ClassDefinition} of the outer POJO which will contain
     *                             the Builder as a static inner class
     * @param javaGeneratorFactory A factory for creating the field and method class names
     * @return a newly constructed {@link BuilderGenerator}
     */
    public BuilderGenerator create(final ClassDefinition classDefinition,
                                   final JavaGeneratorFactory javaGeneratorFactory,
                                   final PluginContext pluginContext) {

        return new BuilderGenerator(
                classDefinition,
                javaGeneratorFactory,
                new BuilderFieldFactory(),
                new BuilderMethodFactory(),
                pluginContext
        );
    }
}
