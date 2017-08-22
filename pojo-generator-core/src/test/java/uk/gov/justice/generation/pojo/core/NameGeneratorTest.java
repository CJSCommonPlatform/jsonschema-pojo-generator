package uk.gov.justice.generation.pojo.core;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class NameGeneratorTest {

    @InjectMocks
    private NameGenerator nameGenerator;

    @Test
    public void shouldParseTheFileNameIntoAValidJavaClassName() throws Exception {

        final File schemaFile = new File("src/test/resources/schemas/object-property-schema.json");

        assertThat(schemaFile.exists(), is(true));

        assertThat(nameGenerator.rootFieldNameFrom(schemaFile), is("objectPropertySchema"));
    }

    @Test
    public void shouldRemoveAnyPrependingDotsFromTheFileName() throws Exception {

        final File schemaFile = new File("src/test/resources/schemas/context.command.do-something-or-other.json");

        assertThat(schemaFile.exists(), is(true));

        assertThat(nameGenerator.rootFieldNameFrom(schemaFile), is("doSomethingOrOther"));
    }

    @Test
    public void shouldFailIfTheFileNameDoesNotHaveTheExtensionJson() throws Exception {

        final File schemaFile = new File("src/test/resources/schemas/object-property-schema");
        assertThat(schemaFile.exists(), is(true));
        try {
            nameGenerator.rootFieldNameFrom(schemaFile);
            fail();
        } catch (final JsonSchemaParseException expected) {
            assertThat(expected.getMessage(), is("Failed to load json schema file '" + schemaFile.getAbsolutePath() + "'. File does not have a '.json' extension"));
        }
    }

    @Test
    public void shouldFailIfTheFileNameIsOnlyTheExtendion() throws Exception {

        final File schemaFile = new File("src/test/resources/schemas/.json");
        try {
            nameGenerator.rootFieldNameFrom(schemaFile);
            fail();
        } catch (final JsonSchemaParseException expected) {
            assertThat(expected.getMessage(), is("Failed to load json schema file '" + schemaFile.getAbsolutePath() + "'. File name is invalid"));
        }
    }

    @Test
    public void shouldFailIfTheFileNameIsOnlyADot() throws Exception {

        final File schemaFile = new File("src/test/resources/schemas/..json");
        try {
            nameGenerator.rootFieldNameFrom(schemaFile);
            fail();
        } catch (final JsonSchemaParseException expected) {
            assertThat(expected.getMessage(), is("Failed to load json schema file '" + schemaFile.getAbsolutePath() + "'. File name is invalid"));
        }
    }

    @Test
    public void shouldParseFileNameIntoEventName() throws Exception {

        final File schemaFile = new File("src/test/resources/schemas/example.events.do-something-or-other.json");

        assertThat(nameGenerator.eventNameFrom(schemaFile), is("example.events.do-something-or-other"));
    }
}