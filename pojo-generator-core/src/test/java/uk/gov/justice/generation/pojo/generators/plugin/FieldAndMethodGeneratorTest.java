package uk.gov.justice.generation.pojo.generators.plugin;

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

import uk.gov.justice.generation.pojo.dom.ClassDefinition;
import uk.gov.justice.generation.pojo.dom.ClassName;
import uk.gov.justice.generation.pojo.dom.FieldDefinition;
import uk.gov.justice.generation.pojo.generators.FieldGenerator;
import uk.gov.justice.generation.pojo.generators.JavaGeneratorFactory;

import java.util.stream.Stream;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.junit.Test;

public class FieldAndMethodGeneratorTest {

    @Test
    public void shouldGenerateTypeSpecForClassDefinitionWithNoFields() throws Exception {
        final JavaGeneratorFactory generatorFactory = mock(JavaGeneratorFactory.class);
        final ClassDefinition classDefinition = new ClassDefinition("address", mock(ClassName.class));

        final TypeSpec.Builder typeSpecBuilder = classBuilder("ClassName");

        new FieldAndMethodGenerator().generateWith(typeSpecBuilder, classDefinition, generatorFactory);

        final TypeSpec typeSpec = typeSpecBuilder.build();

        assertThat(typeSpec.annotations.isEmpty(), is(true));
        assertThat(typeSpec.name, is("ClassName"));
        assertThat(typeSpec.fieldSpecs.size(), is(0));
        assertThat(typeSpec.methodSpecs.size(), is(1));
        assertThat(typeSpec.methodSpecs, hasItem(constructorBuilder().addModifiers(PUBLIC).build()));
    }

    @Test
    public void shouldGenerateTypeSpecForClassDefinitionWithOneField() throws Exception {
        final JavaGeneratorFactory generatorFactory = mock(JavaGeneratorFactory.class);
        final ClassDefinition classDefinition = new ClassDefinition("address", mock(ClassName.class));
        final FieldDefinition fieldDefinition = new FieldDefinition("field", new ClassName(String.class));
        classDefinition.addFieldDefinition(fieldDefinition);

        final FieldSpec fieldSpec = builder(String.class, "field").build();
        final MethodSpec methodSpec = methodBuilder("getField")
                .addModifiers(PUBLIC)
                .returns(String.class)
                .addCode(CodeBlock.builder().addStatement("return $L", "field").build())
                .build();

        final FieldGenerator fieldGenerator = mock(FieldGenerator.class);

        when(generatorFactory.createGeneratorFor(fieldDefinition)).thenReturn(fieldGenerator);
        when(fieldGenerator.generateField()).thenReturn(fieldSpec);
        when(fieldGenerator.generateMethods()).thenReturn(Stream.of(methodSpec));

        final TypeSpec.Builder typeSpecBuilder = classBuilder("ClassName");

        new FieldAndMethodGenerator().generateWith(typeSpecBuilder, classDefinition, generatorFactory);

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