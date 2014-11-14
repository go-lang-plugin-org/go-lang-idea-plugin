package ro.redeul.google.go.lang.psi.typing;

import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeStruct;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructAnonymousField;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;

import java.util.HashMap;
import java.util.Map;

public class GoTypeStruct extends GoTypePsiBacked<GoPsiTypeStruct> implements GoType {

    public GoTypeStruct(GoPsiTypeStruct type) {
        super(type);
    }

    @Override
    public boolean isIdentical(GoType type) {
        if (!(type instanceof GoTypeStruct))
            return false;

        GoTypeStruct struct = (GoTypeStruct) type;

        Map<String, GoType> myNamedFieldsMap = getFieldTypeMap();
        Map<String, GoType> otherNamedFieldsMap = struct.getFieldTypeMap();


        for (Map.Entry<String, GoType> fieldEntry : myNamedFieldsMap.entrySet()) {
            if ( !otherNamedFieldsMap.containsKey(fieldEntry.getKey()))
                return false;

            GoType otherFieldType = otherNamedFieldsMap.get(fieldEntry.getKey());
            if ( otherFieldType == null || !fieldEntry.getValue().isIdentical(otherFieldType))
                return false;

            otherNamedFieldsMap.remove(fieldEntry.getKey());
        }

        return otherNamedFieldsMap.size() == 0;
    }

    protected Map<String, GoType> getFieldTypeMap() {
        GoTypeStructField fields[] = getPsiType().getFields();

        Map<String, GoType> typeMap = new HashMap<String, GoType>();
        for (GoTypeStructField field : fields) {
            GoType type = types().fromPsiType(field.getType());

            for (GoLiteralIdentifier identifier : field.getIdentifiers()) {
                typeMap.put(identifier.getName(), type);
            }
        }

        for (GoTypeStructAnonymousField field : getPsiType().getAnonymousFields()) {
            GoType type = GoTypes.dereference(types().fromPsiType(field.getType()));
            if (type instanceof GoTypeName) {
                typeMap.put(((GoTypeName) type).getName(), type);
            }
        }

        return typeMap;
    }

    @Override
    public <T> T accept(TypeVisitor<T> visitor) {
        return visitor.visitStruct(this);
    }

    @Override
    public String toString() {
        return String.format("struct{ /* ... */ }");
    }
}
