package pl.edu.icm.trurl.generator.writer.feature;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import pl.edu.icm.trurl.generator.CommonTypes;
import pl.edu.icm.trurl.generator.model.BeanMetadata;
import pl.edu.icm.trurl.generator.model.ComponentProperty;
import pl.edu.icm.trurl.generator.model.PropertyType;

import javax.lang.model.element.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GetChildMappersFeature implements Feature {
    private final BeanMetadata beanMetadata;

    public GetChildMappersFeature(BeanMetadata beanMetadata) {
        this.beanMetadata = beanMetadata;
    }

    @Override
    public Stream<FieldSpec> fields() {
        return Stream.empty();
    }

    @Override
    public Stream<MethodSpec> methods() {
        return Stream.of(overrideGetChildMappers(beanMetadata));
    }

    private MethodSpec overrideGetChildMappers(BeanMetadata beanMetadata) {
        List<ComponentProperty> childMappers = beanMetadata
                .getComponentProperties().stream()
                .filter(p -> p.type == PropertyType.EMBEDDED_PROP || p.type == PropertyType.EMBEDDED_LIST)
                .collect(Collectors.toList());
        return MethodSpec.methodBuilder("getChildMappers")
                .returns(CommonTypes.MAPPER_LIST)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addCode(childMappers.isEmpty() ? emptyList() : listMappers(childMappers))
                .build();
    }

    private CodeBlock listMappers(List<ComponentProperty> mapperProperties) {
        return CodeBlock.builder()
                .addStatement("return $T.asList($L)", CommonTypes.ARRAYS, mapperProperties.stream()
                        .map(p -> p.name)
                        .collect(Collectors.joining(", ")))
                .build();
    }

    private CodeBlock emptyList() {
        return CodeBlock.builder()
                .addStatement("return $T.emptyList()", CommonTypes.COLLECTIONS)
                .build();
    }
}
