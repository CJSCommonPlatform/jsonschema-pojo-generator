package uk.gov.justice.generation.pojo.generators;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.capitalize;

import uk.gov.justice.generation.pojo.core.GenerationContext;
import uk.gov.justice.generation.pojo.dom.ClassDefinition;
import uk.gov.justice.generation.pojo.dom.Definition;
import uk.gov.justice.generation.pojo.dom.EnumDefinition;
import uk.gov.justice.generation.pojo.dom.FieldDefinition;
import uk.gov.justice.generation.pojo.plugin.PluginProvider;
import uk.gov.justice.generation.pojo.plugin.classmodifying.PluginContext;

import java.util.List;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

/**
 * Factory for creating the correct generator for the specified {@link Definition}
 */
public class JavaGeneratorFactory {

    private final ClassNameFactory classNameFactory;

    public JavaGeneratorFactory(final ClassNameFactory classNameFactory) {
        this.classNameFactory = classNameFactory;
    }

    public ElementGeneratable createGeneratorFor(final Definition definition, final PluginContext pluginContext) {

        if (definition.getClass() == ClassDefinition.class || definition.getClass() == EnumDefinition.class) {
            return new ElementGenerator(definition, classNameFactory, pluginContext);
        }

        return new FieldGenerator((FieldDefinition) definition, classNameFactory, pluginContext);
    }

    public List<ClassGeneratable> createClassGeneratorsFor(final List<Definition> definitions,
                                                           final PluginProvider pluginProvider,
                                                           final PluginContext pluginContext,
                                                           final GenerationContext generationContext) {
        return definitions.stream()
                .filter(this::isClassOrEnum)
                .filter(definition -> isNotHardCoded(definition, generationContext.getIgnoredClassNames()))
                .map(definition -> getClassGeneratable(pluginProvider, pluginContext, definition))
                .collect(toList());
    }

    /**
     * Generate to correct return type/parameter type for the specified {@link Definition}
     *
     * @param definition    The definition for which to generate the correct return type
     * @param pluginContext The {@link PluginContext}
     * @return The correct type for returns and parameters
     */
    public TypeName createTypeNameFrom(final Definition definition, final PluginContext pluginContext) {
        return classNameFactory.createTypeNameFrom(definition, pluginContext);
    }

    public ClassName createClassNameFrom(final ClassDefinition classDefinition) {
        return classNameFactory.createClassNameFrom(classDefinition);
    }

    private boolean isClassOrEnum(final Definition definition) {
        return EnumDefinition.class.isInstance(definition) || ClassDefinition.class.isInstance(definition);
    }

    private boolean isNotHardCoded(final Definition definition, final List<String> hardCodedClassNames) {
        final String className = capitalize(definition.getFieldName());
        return !hardCodedClassNames.contains(className);
    }

    private ClassGeneratable getClassGeneratable(
            final PluginProvider pluginProvider,
            final PluginContext pluginContext,
            final Definition definition) {

        if (definition.getClass() == EnumDefinition.class) {
            return new EnumGenerator((EnumDefinition) definition);
        }

        return new ClassGenerator((ClassDefinition) definition,
                classNameFactory,
                pluginProvider,
                pluginContext);
    }
}
