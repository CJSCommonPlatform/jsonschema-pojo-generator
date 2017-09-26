package uk.gov.justice.generation;

import uk.gov.justice.generation.pojo.generators.ClassNameFactory;
import uk.gov.justice.generation.pojo.generators.JavaGeneratorFactory;

public class JavaGeneratorFactoryProvider {

    public JavaGeneratorFactory getJavaGeneratorFactory(final ClassNameFactory classNameFactory) {
        return new JavaGeneratorFactory(classNameFactory);
    }
}
