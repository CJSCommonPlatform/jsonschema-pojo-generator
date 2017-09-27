package uk.gov.justice.generation;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.justice.generation.utils.ReflectionUtil.fieldValue;

import uk.gov.justice.generation.pojo.core.GenerationContext;
import uk.gov.justice.generation.pojo.generators.ClassNameFactory;
import uk.gov.justice.generation.pojo.generators.JavaGeneratorFactory;
import uk.gov.justice.generation.pojo.plugin.PluginProvider;

import org.junit.Test;

public class JavaGeneratorFactoryProviderTest {

    @Test
    public void shouldProvideJavaGeneratorFactory() throws Exception {
        final GenerationContext generationContext = mock(GenerationContext.class);
        final PluginProvider pluginProvider = mock(PluginProvider.class);
        final ClassNameFactoryProvider classNameFactoryProvider = mock(ClassNameFactoryProvider.class);
        final ClassNameFactory classNameFactory = mock(ClassNameFactory.class);

        when(classNameFactoryProvider.create(generationContext, pluginProvider)).thenReturn(classNameFactory);

        final JavaGeneratorFactory javaGeneratorFactory = new JavaGeneratorFactoryProvider(classNameFactoryProvider)
                .create(generationContext, pluginProvider);

        assertThat(fieldValue(javaGeneratorFactory, "classNameFactory"), is(classNameFactory));
    }
}
