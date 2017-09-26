package uk.gov.justice.generation.pojo.plugin.typemodifying.custom;

import static com.squareup.javapoet.ClassName.get;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import uk.gov.justice.generation.pojo.plugin.classmodifying.PluginContext;
import uk.gov.justice.generation.pojo.visitor.ReferenceValue;

import java.util.Optional;
import java.util.UUID;

import com.squareup.javapoet.ClassName;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CustomReturnTypeMapperTest {

    @Mock
    private FullyQualifiedNameToClassNameConverter fullyQualifiedNameToClassNameConverter;

    @InjectMocks
    private CustomReturnTypeMapper customReturnTypeMapper;

    @Test
    public void shouldGetTheCustomTypeMappingFromGeneratorPropertiesAndConvertToAClassName() throws Exception {

        final String mappingPropertyName = "uuid";
        final ReferenceValue referenceValue = new ReferenceValue("#/definitions", mappingPropertyName);
        final Optional<String> mappingPropertyValue = of("java.util.UUID");
        final ClassName uuidClassName = get(UUID.class);

        final PluginContext pluginContext = mock(PluginContext.class);

        when(pluginContext.typeMappingOf(mappingPropertyName)).thenReturn(mappingPropertyValue);
        when(fullyQualifiedNameToClassNameConverter.convert(mappingPropertyValue.get())).thenReturn(uuidClassName);

        final Optional<ClassName> className = customReturnTypeMapper.customType(referenceValue, pluginContext);

        if (className.isPresent()) {
            assertThat(className.get(), is(uuidClassName));
        } else {
            fail();
        }
    }

    @Test
    public void shouldReturnEmptyIfNoCustomMappingExistsForTheReferenceValueName() throws Exception {

        final String mappingPropertyName = "uuid";
        final ReferenceValue referenceValue = new ReferenceValue("#/definitions", mappingPropertyName);
        final Optional<String> mappingPropertyValue = empty();
        
        final PluginContext pluginContext = mock(PluginContext.class);


        when(pluginContext.typeMappingOf(mappingPropertyName)).thenReturn(mappingPropertyValue);

        final Optional<ClassName> className = customReturnTypeMapper.customType(referenceValue, pluginContext);

        assertThat(className.isPresent(), is(false));

        verifyZeroInteractions(fullyQualifiedNameToClassNameConverter);
    }
}
