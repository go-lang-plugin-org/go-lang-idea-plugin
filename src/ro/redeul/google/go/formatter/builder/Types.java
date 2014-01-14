package ro.redeul.google.go.formatter.builder;

import com.intellij.formatting.Block;
import ro.redeul.google.go.formatter.blocks.*;
import ro.redeul.google.go.lang.psi.types.*;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;

import static ro.redeul.google.go.formatter.blocks.GoBlockUtil.CustomSpacings;

/**
 * TODO: Document this
 * <p/>
 * Created on Jan-13-2014 22:25
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
abstract class Types extends Statements {

    @Override
    public Block visitTypeArray(GoPsiTypeArray type, State s) {
        return new Code<GoPsiTypeArray>(type, s.settings, null, s.alignment, s.alignmentsMap)
            .setCustomSpacing(CustomSpacings.TYPE_ARRAY);
    }

    @Override
    public Block visitTypePointer(GoPsiTypePointer type, State s) {
        return new Code<GoPsiTypePointer>(type, s.settings, null, s.alignment, s.alignmentsMap)
            .setCustomSpacing(CustomSpacings.TYPE_POINTER);
    }

    @Override
    public Block visitTypeSlice(GoPsiTypeSlice type, State s) {
        return new Code<GoPsiTypeSlice>(type, s.settings, null, s.alignment, s.alignmentsMap)
            .setCustomSpacing(CustomSpacings.TYPE_SLICE);
    }

    @Override
    public Block visitTypeMap(GoPsiTypeMap type, State s) {
        return new Code<GoPsiTypeMap>(type, s.settings, null, s.alignment, s.alignmentsMap)
            .setCustomSpacing(CustomSpacings.TYPE_MAP);
    }

    @Override
    public Block visitTypeChannel(GoPsiTypeChannel type, State s) {
        return new Code<GoPsiTypeChannel>(type, s.settings, null, s.alignment, s.alignmentsMap)
            .setCustomSpacing(CustomSpacings.TYPE_CHANNEL);
    }

    @Override
    public Block visitTypeStruct(GoPsiTypeStruct type, State s) {
        return new TypeStruct(type, s.settings, s.alignment, s.alignmentsMap);
    }

    @Override
    public Block visitTypeStructField(GoTypeStructField typeStructField, State s) {
        return new TypeStructField(typeStructField, s.settings, s.indent, s.alignmentsMap);
    }

    @Override
    public Block visitTypeInterface(GoPsiTypeInterface type, State s) {
        return new TypeInterface(type, s.settings, s.alignment, s.alignmentsMap);
    }

    //    if (psi instanceof GoPsiTypeFunction)
//        return new CodeBlock<GoPsiTypeFunction>((GoPsiTypeFunction) psi, settings, null, alignment, alignmentsMap)
//        .setCustomSpacing(
//        CustomSpacings.TYPE_FUNCTION);
//
    @Override
    public Block visitTypeParenthesized(GoPsiTypeParenthesized type, State s) {
        return new Code<GoPsiTypeParenthesized>(type, s.settings, null, s.alignment, s.alignmentsMap)
            .setCustomSpacing(CustomSpacings.TYPE_PARENTHESISED);
    }

}
