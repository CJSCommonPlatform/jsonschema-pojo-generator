package uk.gov.justice.generation.pojo.generators.plugin.classgenerator;

import static com.squareup.javapoet.TypeName.LONG;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.STATIC;

import uk.gov.justice.generation.pojo.dom.ClassDefinition;

import java.io.Serializable;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeSpec;

public class MakeClassSerializablePlugin implements ClassModifyingPlugin {

    private static final String SERIAL_VERSION_FIELD_NAME = "serialVersionUID";
    private static final String SERIAL_VERSION_VALUE = "1L";

    @Override
    public TypeSpec.Builder generateWith(final TypeSpec.Builder typeSpecBuilder,
                                         final ClassDefinition classDefinition,
                                         final PluginContext pluginContext) {

        typeSpecBuilder.addSuperinterface(Serializable.class)
                .addField(FieldSpec
                        .builder(LONG, SERIAL_VERSION_FIELD_NAME, PRIVATE, STATIC, FINAL)
                        .initializer(SERIAL_VERSION_VALUE)
                        .build());

        return typeSpecBuilder;
    }
}