package uk.gov.justice.generation.pojo.generators.plugin;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

public class DefaultPluginProviderTest {

    @Test
    @SuppressWarnings("unchecked")
    public void shouldProvideDefaultListOfPluginClassGenerators() throws Exception {
        final List<PluginClassGeneratable> pluginClassGenerators = new DefaultPluginProvider().pluginClassGenerators();

        assertThat(pluginClassGenerators.size(), is(3));
        assertThat(pluginClassGenerators, hasItems(
                instanceOf(EventAnnotationGenerator.class),
                instanceOf(SerializableGenerator.class),
                instanceOf(FieldAndMethodGenerator.class)));
    }
}