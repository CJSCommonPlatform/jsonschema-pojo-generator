package uk.gov.justice.generation.pojo.plugin.factory;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import uk.gov.justice.generation.pojo.plugin.PluginProviderException;
import uk.gov.justice.generation.pojo.plugin.classmodifying.ClassModifyingPlugin;
import uk.gov.justice.generation.pojo.plugin.namegeneratable.NameGeneratablePlugin;
import uk.gov.justice.generation.pojo.plugin.typemodifying.TypeModifyingPlugin;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PluginTypeSorterTest {

    @InjectMocks
    private PluginTypeSorter pluginTypeSorter;

    @Test
    public void shouldSortTheListOfPluginsByTheirPluginType() throws Exception {

        final ClassModifyingPlugin classModifyingPlugin_1 = mock(ClassModifyingPlugin.class);
        final ClassModifyingPlugin classModifyingPlugin_2 = mock(ClassModifyingPlugin.class);
        final ClassModifyingPlugin classModifyingPlugin_3 = mock(ClassModifyingPlugin.class);

        final TypeModifyingPlugin typeModifyingPlugin_1 = mock(TypeModifyingPlugin.class);
        final TypeModifyingPlugin typeModifyingPlugin_2 = mock(TypeModifyingPlugin.class);
        final TypeModifyingPlugin typeModifyingPlugin_3 = mock(TypeModifyingPlugin.class);

        final NameGeneratablePlugin nameGeneratablePlugin_1 = mock(NameGeneratablePlugin.class);
        final NameGeneratablePlugin nameGeneratablePlugin_2 = mock(NameGeneratablePlugin.class);
        final NameGeneratablePlugin nameGeneratablePlugin_3 = mock(NameGeneratablePlugin.class);

        final List<Object> plugins = asList(
                classModifyingPlugin_1,
                typeModifyingPlugin_1,
                nameGeneratablePlugin_1,
                classModifyingPlugin_2,
                typeModifyingPlugin_2,
                nameGeneratablePlugin_2,
                classModifyingPlugin_3,
                typeModifyingPlugin_3,
                nameGeneratablePlugin_3
        );

        final Map<Class<?>, List<Object>> pluginsByType = pluginTypeSorter.sortByType(plugins);

        assertThat(pluginsByType.size(), is(3));
        
        assertThat(pluginsByType.containsKey(ClassModifyingPlugin.class), is(true));
        assertThat(pluginsByType.containsKey(TypeModifyingPlugin.class), is(true));
        assertThat(pluginsByType.containsKey(NameGeneratablePlugin.class), is(true));

        final List<Object> classModifyingPlugins = pluginsByType.get(ClassModifyingPlugin.class);
        final List<Object> typeModifyingPlugins = pluginsByType.get(TypeModifyingPlugin.class);
        final List<Object> nameGeneratablePlugins = pluginsByType.get(NameGeneratablePlugin.class);

        assertThat(classModifyingPlugins, hasItem(classModifyingPlugin_1));
        assertThat(classModifyingPlugins, hasItem(classModifyingPlugin_2));
        assertThat(classModifyingPlugins, hasItem(classModifyingPlugin_3));

        assertThat(typeModifyingPlugins, hasItem(typeModifyingPlugin_1));
        assertThat(typeModifyingPlugins, hasItem(typeModifyingPlugin_2));
        assertThat(typeModifyingPlugins, hasItem(typeModifyingPlugin_3));

        assertThat(nameGeneratablePlugins, hasItem(nameGeneratablePlugin_1));
        assertThat(nameGeneratablePlugins, hasItem(nameGeneratablePlugin_2));
        assertThat(nameGeneratablePlugins, hasItem(nameGeneratablePlugin_3));
    }

    @Test
    public void shouldFailIfTheClassIsNotAPluginClass() throws Exception {

        try {
            final String plugin = "This is not a plugin";
            pluginTypeSorter.sortByType(singletonList(plugin));
            fail();
        } catch (final PluginProviderException expected) {
            assertThat(expected.getMessage(), is("Incorrect Class Type, Class name: java.lang.String, does not implement ClassModifyingPlugin or TypeModifyingPlugin or NameGeneratablePlugin."));
        }
    }
}