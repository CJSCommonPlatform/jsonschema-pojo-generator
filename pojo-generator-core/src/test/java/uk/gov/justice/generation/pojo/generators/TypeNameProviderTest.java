package uk.gov.justice.generation.pojo.generators;

import static com.squareup.javapoet.ClassName.get;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import uk.gov.justice.generation.pojo.core.GenerationContext;
import uk.gov.justice.generation.pojo.dom.ClassDefinition;
import uk.gov.justice.generation.pojo.dom.Definition;
import uk.gov.justice.generation.pojo.dom.ReferenceDefinition;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TypeNameProviderTest {

    @Mock
    private GenerationContext generationContext;

    @InjectMocks
    private TypeNameProvider typeNameProvider;

    @Test
    public void shouldGetTheCorrectTypeNameForAnArray() throws Exception {

        final ClassName stringTypeName = get(String.class);

        final ClassDefinition classDefinition = mock(ClassDefinition.class);
        final Definition childDefinition = mock(Definition.class);
        final ClassNameFactory classNameFactory = mock(ClassNameFactory.class);

        when(classDefinition.getFieldDefinitions()).thenReturn(singletonList(childDefinition));
        when(classNameFactory.createTypeNameFrom(childDefinition)).thenReturn(stringTypeName);

        final TypeName typeName = typeNameProvider.typeNameForArray(classDefinition, classNameFactory);

        assertThat(typeName.toString(), is("java.util.List<java.lang.String>"));
    }

    @Test
    public void shouldFailIfNoChildDefinitionsWhenGettingTheCorrectTypeNameForAnArray() throws Exception {

        final ClassDefinition classDefinition = mock(ClassDefinition.class);
        final ClassNameFactory classNameFactory = mock(ClassNameFactory.class);

        when(classDefinition.getFieldDefinitions()).thenReturn(emptyList());
        when(classDefinition.getFieldName()).thenReturn("myListOfStringsField");

        try {
            typeNameProvider.typeNameForArray(classDefinition, classNameFactory);
            fail();
        } catch (final GenerationException expected) {
            assertThat(expected.getMessage(), is("No definition present for array types. For field: myListOfStringsField"));
        }
    }

    @Test
    public void shouldGReturnCorrectTypeNameForReference() throws Exception {

        final ClassName stringTypeName = get(String.class);

        final ReferenceDefinition referenceDefinition = mock(ReferenceDefinition.class);
        final Definition referredDefinition = mock(Definition.class);
        final ClassNameFactory classNameFactory = mock(ClassNameFactory.class);

        when(referenceDefinition.getFieldDefinitions()).thenReturn(singletonList(referredDefinition));
        when(classNameFactory.createTypeNameFrom(referredDefinition)).thenReturn(stringTypeName);
        when(referenceDefinition.getReferenceValue()).thenReturn("#/definitions/date");

        final TypeName typeName = typeNameProvider.typeNameForReference(referenceDefinition, classNameFactory);

        assertThat(typeName.toString(), is("java.lang.String"));
    }

    @Test
    public void shouldFailIfNoReferredDefinitionInReference() throws Exception {

        final ReferenceDefinition referenceDefinition = mock(ReferenceDefinition.class);
        final ClassNameFactory classNameFactory = mock(ClassNameFactory.class);

        when(referenceDefinition.getFieldDefinitions()).thenReturn(emptyList());
        when(referenceDefinition.getFieldName()).thenReturn("startDate");

        try {
            typeNameProvider.typeNameForReference(referenceDefinition, classNameFactory);
            fail();
        } catch (final GenerationException expected) {
            assertThat(expected.getMessage(), is("No definition present for reference type. For field: startDate"));
        }
    }

    @Test
    public void shouldReturnUuidIfEndOfReferenceValueMapsToUuidClassName() throws Exception {

        final ReferenceDefinition referenceDefinition = mock(ReferenceDefinition.class);
        final Definition referredDefinition = mock(Definition.class);
        final ClassNameFactory classNameFactory = mock(ClassNameFactory.class);

        when(referenceDefinition.getFieldDefinitions()).thenReturn(singletonList(referredDefinition));
        when(referenceDefinition.getReferenceValue()).thenReturn("#/definitions/UUID");

        final TypeName typeName = typeNameProvider.typeNameForReference(referenceDefinition, classNameFactory);

        assertThat(typeName.toString(), is("java.util.UUID"));
    }

    @Test
    public void shouldReturnUuidIfEndOfReferenceValueMapsToZonedDateTimeClassName() throws Exception {

        final ReferenceDefinition referenceDefinition = mock(ReferenceDefinition.class);
        final Definition referredDefinition = mock(Definition.class);
        final ClassNameFactory classNameFactory = mock(ClassNameFactory.class);

        when(referenceDefinition.getFieldDefinitions()).thenReturn(singletonList(referredDefinition));
        when(referenceDefinition.getReferenceValue()).thenReturn("#/definitions/ZonedDateTime");

        final TypeName typeName = typeNameProvider.typeNameForReference(referenceDefinition, classNameFactory);

        assertThat(typeName.toString(), is("java.time.ZonedDateTime"));
    }

    @Test
    public void shouldGetTheCorrectTypeNameForAString() throws Exception {

        final TypeName typeName = typeNameProvider.typeNameForString();

        assertThat(typeName.toString(), is("java.lang.String"));
    }

    @Test
    public void shouldGetTheCorrectTypeNameForAClass() throws Exception {

        final ClassDefinition classDefinition = mock(ClassDefinition.class);

        when(classDefinition.getFieldName()).thenReturn("address");
        when(generationContext.getPackageName()).thenReturn("org.bloggs.fred");

        final TypeName typeName = typeNameProvider.typeNameForClass(classDefinition);

        assertThat(typeName.toString(), is("org.bloggs.fred.Address"));
    }

    @Test
    public void shouldGetTheCorrectTypeNameForANumber() throws Exception {

        final TypeName typeName = typeNameProvider.typeNameForNumber();

        assertThat(typeName.toString(), is("java.math.BigDecimal"));
    }

    @Test
    public void shouldGetTheCorrectTypeNameForAnInteger() throws Exception {

        final TypeName typeName = typeNameProvider.typeNameForInteger();

        assertThat(typeName.toString(), is("java.lang.Integer"));
    }

    @Test
    public void shouldGetTheCorrectTypeNameForABoolean() throws Exception {

        final TypeName typeName = typeNameProvider.typeNameForBoolean();

        assertThat(typeName.toString(), is("java.lang.Boolean"));
    }
}