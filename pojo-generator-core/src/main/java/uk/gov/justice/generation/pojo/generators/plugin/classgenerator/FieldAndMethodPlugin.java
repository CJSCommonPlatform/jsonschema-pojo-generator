package uk.gov.justice.generation.pojo.generators.plugin.classgenerator;

import static com.squareup.javapoet.MethodSpec.constructorBuilder;
import static java.util.stream.Collectors.toList;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;

import uk.gov.justice.generation.pojo.core.GenerationContext;
import uk.gov.justice.generation.pojo.dom.ClassDefinition;
import uk.gov.justice.generation.pojo.dom.Definition;
import uk.gov.justice.generation.pojo.generators.AdditionalPropertiesGenerator;
import uk.gov.justice.generation.pojo.generators.ClassNameFactory;
import uk.gov.justice.generation.pojo.generators.ElementGeneratable;
import uk.gov.justice.generation.pojo.generators.JavaGeneratorFactory;

import java.util.List;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

public class FieldAndMethodPlugin implements ClassGeneratorPlugin {

    private final AdditionalPropertiesGenerator additionalPropertiesGenerator = new AdditionalPropertiesGenerator();

    @Override
    public TypeSpec.Builder generateWith(final TypeSpec.Builder typeSpecBuilder,
                                         final ClassDefinition classDefinition,
                                         final JavaGeneratorFactory javaGeneratorFactory,
                                         final ClassNameFactory classNameFactory,
                                         final GenerationContext generationContext) {

        final List<Definition> fieldDefinitions = classDefinition.getFieldDefinitions();

        final List<ElementGeneratable> fieldGenerators = fieldDefinitions
                .stream()
                .map(javaGeneratorFactory::createGeneratorFor)
                .collect(toList());

        final List<FieldSpec> fields = fieldGenerators
                .stream()
                .map(ElementGeneratable::generateField)
                .collect(toList());

        final List<MethodSpec> methods = fieldGenerators
                .stream()
                .flatMap(ElementGeneratable::generateMethods)
                .collect(toList());

        typeSpecBuilder.addMethod(buildConstructor(fieldDefinitions, classNameFactory))
                .addFields(fields)
                .addMethods(methods);

        if (classDefinition.allowAdditionalProperties()) {

            final FieldSpec additionalProperties = additionalPropertiesGenerator.generateField();
            final List<MethodSpec> gettersAndSetters = additionalPropertiesGenerator
                    .generateMethods()
                    .collect(toList());

            typeSpecBuilder.addField(additionalProperties);
            typeSpecBuilder.addMethods(gettersAndSetters);
        }

        return typeSpecBuilder;
    }

    private MethodSpec buildConstructor(final List<Definition> definitions, final ClassNameFactory classNameFactory) {
        final List<String> fieldNames = definitions.stream().map(Definition::getFieldName).collect(toList());

        return constructorBuilder()
                .addModifiers(PUBLIC)
                .addParameters(constructorParameters(definitions, classNameFactory))
                .addCode(constructorStatements(fieldNames))
                .build();
    }

    private CodeBlock constructorStatements(final List<String> names) {
        final CodeBlock.Builder builder = CodeBlock.builder();

        names.forEach(fieldName -> builder.addStatement("this.$N = $N", fieldName, fieldName));

        return builder.build();
    }

    private List<ParameterSpec> constructorParameters(final List<Definition> definitions, final ClassNameFactory classNameFactory) {
        return definitions.stream()
                .map(definition -> ParameterSpec.builder(classNameFactory.createTypeNameFrom(definition), definition.getFieldName(), FINAL).build())
                .collect(toList());
    }
}