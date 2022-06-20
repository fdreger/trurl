package pl.edu.icm.trurl.generator.writer.feature;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import pl.edu.icm.trurl.generator.CommonTypes;
import pl.edu.icm.trurl.generator.model.BeanMetadata;
import pl.edu.icm.trurl.generator.model.ComponentFeature;
import pl.edu.icm.trurl.generator.model.ComponentProperty;

import javax.lang.model.element.Modifier;
import java.util.stream.Stream;

public class IsModifiedFeature implements Feature {
    private final BeanMetadata beanMetadata;

    public IsModifiedFeature(BeanMetadata beanMetadata) {
        this.beanMetadata = beanMetadata;
    }

    @Override
    public Stream<FieldSpec> fields() {
        return Stream.empty();
    }

    @Override
    public Stream<MethodSpec> methods() {
        return Stream.of(overrideIsModified(beanMetadata));
    }

    private MethodSpec overrideIsModified(BeanMetadata beanMetadata) {
        MethodSpec.Builder methodSpec = MethodSpec
                .methodBuilder("isModified")
                .addParameter(beanMetadata.componentName, "component")
                .addParameter(TypeName.INT, "row")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(TypeName.BOOLEAN);

        if (beanMetadata.componentFeatures.contains(ComponentFeature.IS_DIRTY_MARKED)) {
            methodSpec.addStatement("return component.isDirty()");
        } else {
            generateAllChecks(beanMetadata, methodSpec);
            methodSpec.addStatement("return false");
        }
        return methodSpec.build();
    }

    private void generateAllChecks(BeanMetadata beanMetadata, MethodSpec.Builder methodSpec) {
        for (ComponentProperty property : beanMetadata.getComponentProperties()) {
            if (property.synthetic) continue;
            switch (property.type) {
                case INT_PROP:
                    methodSpec.addStatement("if ($L.getInt(row) != component.$L()) return true", property.name, property.getterName);
                    break;
                case BOOLEAN_PROP:
                    methodSpec.addStatement("if ($L.getBoolean(row) != component.$L()) return true", property.name, property.getterName);
                    break;
                case BYTE_PROP:
                    methodSpec.addStatement("if ($L.getByte(row) != component.$L()) return true", property.name, property.getterName);
                    break;
                case DOUBLE_PROP:
                    methodSpec.addStatement("if ($L.getDouble(row) != component.$L()) return true", property.name, property.getterName);
                    break;
                case ENUM_PROP:
                    methodSpec.addStatement("if ($L.getEnum(row) != component.$L()) return true", property.name, property.getterName);
                    break;
                case FLOAT_PROP:
                    methodSpec.addStatement("if ($L.getFloat(row) !=  component.$L()) return true", property.name, property.getterName);
                    break;
                case SHORT_PROP:
                    methodSpec.addStatement("if ($L.getShort(row) != component.$L()) return true", property.name, property.getterName);
                    break;
                case STRING_PROP:
                    methodSpec.addStatement("if (!$T.equals($L.getString(row), component.$L())) return true", CommonTypes.LANG_OBJECTS, property.name, property.getterName);
                    break;
                case ENTITY_LIST_PROP:
                    methodSpec.addStatement("if (!$L.isEqual(row, component.$L().size(), (idx) -> component.$L().get(idx).getId())) return true", property.name, property.getterName, property.getterName);
                    break;
                case ENTITY_PROP:
                    methodSpec.addStatement("if (!$L.isEqual(row, component.$L())) return true", property.name, property.getterName);
                    break;
                case EMBEDDED_LIST:
                    createEmbeddedList(methodSpec, property);
                    break;
                case EMBEDDED_PROP:
                    methodSpec
                            .addCode(CodeBlock.builder()
                                    .beginControlFlow("")
                                    .addStatement("boolean targetPresent = $L.isPresent(row)", property.name)
                                    .addStatement("Object embed = component.$L()", property.getterName)
                                    .addStatement("if (targetPresent && embed == null) return true")
                                    .addStatement("if (!targetPresent && embed != null) return true")
                                    .addStatement("if (targetPresent && embed != null && $L.isModified(component.$L(), row)) return true", property.name, property.getterName)
                                    .endControlFlow()
                                    .build());
                    break;
                default:
                    throw new IllegalStateException("Unknown attribute type " + property.type);
            }
        }
    }


    private void createEmbeddedList(MethodSpec.Builder methodSpec, ComponentProperty property) {
        {
            methodSpec
                    .addCode(
                            CodeBlock.builder()
                                    .beginControlFlow("")
                                    .addStatement("int size = component.$L().size()", property.getterName)
                                    .addStatement("if ($L_start.isEmpty(row) && size > 0) return true", property.name)
                                    .addStatement("int start = $L_start.getInt(row)", property.name)
                                    .beginControlFlow("for (int i = 0; i < size; i++)")
                                    .addStatement("if ($L.isModified(component.$L().get(i), start + i)) return true", property.name, property.getterName)
                                    .endControlFlow()
                                    .addStatement("byte length = $L_length.getByte(row)", property.name)
                                    .addStatement("if (length > size && $L.isPresent(start + size)) return true", property.name)
                                    .endControlFlow()
                                    .build()
                    );
        }
    }
}
