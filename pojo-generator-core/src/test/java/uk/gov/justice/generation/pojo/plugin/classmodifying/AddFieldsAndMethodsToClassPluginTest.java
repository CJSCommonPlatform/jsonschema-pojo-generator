package uk.gov.justice.generation.pojo.plugin.classmodifying;

import static com.squareup.javapoet.FieldSpec.builder;
import static com.squareup.javapoet.MethodSpec.constructorBuilder;
import static com.squareup.javapoet.MethodSpec.methodBuilder;
import static com.squareup.javapoet.TypeSpec.classBuilder;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.justice.generation.pojo.dom.DefinitionType.CLASS;
import static uk.gov.justice.generation.pojo.dom.DefinitionType.STRING;

import uk.gov.justice.generation.pojo.dom.ClassDefinition;
import uk.gov.justice.generation.pojo.dom.FieldDefinition;
import uk.gov.justice.generation.pojo.generators.FieldGenerator;
import uk.gov.justice.generation.pojo.generators.JavaGeneratorFactory;

import java.util.stream.Stream;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AddFieldsAndMethodsToClassPluginTest {

    @Mock
    private JavaGeneratorFactory generatorFactory;

    @Mock
    private PluginContext pluginContext;

    @Test
    public void shouldGenerateTypeSpecForClassDefinitionWithNoFields() throws Exception {
        final ClassDefinition classDefinition = new ClassDefinition(CLASS, "address");

        final TypeSpec.Builder typeSpecBuilder = classBuilder("ClassName");

        when(pluginContext.getJavaGeneratorFactory()).thenReturn(generatorFactory);

        new AddFieldsAndMethodsToClassPlugin()
                .generateWith(
                        typeSpecBuilder,
                        classDefinition,
                        pluginContext);

        final TypeSpec typeSpec = typeSpecBuilder.build();

        assertThat(typeSpec.annotations.isEmpty(), is(true));
        assertThat(typeSpec.name, is("ClassName"));
        assertThat(typeSpec.fieldSpecs.size(), is(0));
        assertThat(typeSpec.methodSpecs.size(), is(1));
        assertThat(typeSpec.methodSpecs, hasItem(constructorBuilder().addModifiers(PUBLIC).build()));
    }

    @Test
    public void shouldGenerateTypeSpecForClassDefinitionWithOneField() throws Exception {
        final ClassDefinition classDefinition = new ClassDefinition(CLASS, "address");
        final FieldDefinition fieldDefinition = new FieldDefinition(STRING, "field");
        classDefinition.addFieldDefinition(fieldDefinition);

        final FieldSpec fieldSpec = builder(String.class, "field").build();
        final MethodSpec methodSpec = methodBuilder("getField")
                .addModifiers(PUBLIC)
                .returns(String.class)
                .addCode(CodeBlock.builder().addStatement("return $L", "field").build())
                .build();

        final FieldGenerator fieldGenerator = mock(FieldGenerator.class);
        final PluginContext pluginContext = mock(PluginContext.class);

        when(pluginContext.getJavaGeneratorFactory()).thenReturn(generatorFactory);
        when(generatorFactory.createGeneratorFor(fieldDefinition, pluginContext)).thenReturn(fieldGenerator);
        when(fieldGenerator.generateField()).thenReturn(fieldSpec);
        when(fieldGenerator.generateMethods()).thenReturn(Stream.of(methodSpec));
        when(generatorFactory.createTypeNameFrom(fieldDefinition, pluginContext)).thenReturn(ClassName.get(String.class));

        final TypeSpec.Builder typeSpecBuilder = classBuilder("ClassName");

        new AddFieldsAndMethodsToClassPlugin()
                .generateWith(
                        typeSpecBuilder,
                        classDefinition,
                        pluginContext);

        final TypeSpec typeSpec = typeSpecBuilder.build();

        assertThat(typeSpec.name, is("ClassName"));
        assertThat(typeSpec.fieldSpecs.size(), is(1));
        assertThat(typeSpec.fieldSpecs, hasItem(fieldSpec));
        assertThat(typeSpec.methodSpecs.size(), is(2));
        assertThat(typeSpec.methodSpecs, hasItems(
                constructorBuilder()
                        .addModifiers(PUBLIC)
                        .addParameter(String.class, "field", FINAL)
                        .addStatement("this.field = field")
                        .build(),
                methodSpec)
        );
    }
}
