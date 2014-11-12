package ro.redeul.google.go.lang.psi.impl.types.struct;

import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeStruct;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructAnonymousField;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructPromotedFields;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeName;
import ro.redeul.google.go.lang.psi.typing.GoTypePointer;
import ro.redeul.google.go.lang.psi.typing.GoTypeStruct;
import ro.redeul.google.go.lang.psi.typing.GoTypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PromotedFieldsDiscover {
    private final Map<String, List<GoLiteralIdentifier>> namedFieldsMap = new HashMap<String, List<GoLiteralIdentifier>>();
    private final Map<String, List<GoTypeStructAnonymousField>> anonymousFieldsMap = new HashMap<String, List<GoTypeStructAnonymousField>>();

    private final GoPsiTypeStruct struct;
    private final Set<String> ignoreNames;

    public PromotedFieldsDiscover(GoPsiTypeStruct struct) {
        this.struct = struct;
        ignoreNames = new HashSet<String>();
    }

    public GoTypeStructPromotedFields getPromotedFields() {
        discover();
        return new GoTypeStructPromotedFields(getNamedFields(), getAnonymousFields());
    }

    private boolean ignore(GoTypeStructAnonymousField field) {
        return ignoreNames.contains(field.getFieldName());
    }

    private boolean ignore(GoLiteralIdentifier identifier) {
        return ignoreNames.contains(identifier.getName());
    }

    private GoLiteralIdentifier[] getNamedFields() {
        List<GoLiteralIdentifier> namedFields = new ArrayList<GoLiteralIdentifier>();
        for (List<GoLiteralIdentifier> identifiers : namedFieldsMap.values()) {
            if (identifiers.size() > 0 && !ignore(identifiers.get(0))) {
                namedFields.add(identifiers.get(0));
            }
        }

        return namedFields.toArray(new GoLiteralIdentifier[namedFields.size()]);
    }

    private GoTypeStructAnonymousField[] getAnonymousFields() {
        List<GoTypeStructAnonymousField> anonymousFields = new ArrayList<GoTypeStructAnonymousField>();
        for (List<GoTypeStructAnonymousField> fields : anonymousFieldsMap.values()) {
            if (fields.size() == 1) {
                anonymousFields.add(fields.get(0));
            }
        }
        return anonymousFields.toArray(new GoTypeStructAnonymousField[anonymousFields.size()]);
    }

    private void discover() {
        namedFieldsMap.clear();
        anonymousFieldsMap.clear();
        ignoreNames.clear();
        ignoreNames.addAll(getDirectFieldNameSet());
        processStruct(struct);
    }

    private void processAnonymousField(GoTypeStructAnonymousField field) {

        GoType type = GoTypes.fromPsi(field.getType());

        // we look at the field type and we dereference it once if necessary
        if (type instanceof GoTypePointer)
            type = ((GoTypePointer)type).getTargetType();

        // we should have a type name now
        if (!(type instanceof GoTypeName))
            return;

        type = type.getUnderlyingType();
        if ( type == null || !(type instanceof GoTypeStruct))
            return;

        GoTypeStruct typeStruct = (GoTypeStruct) type;

        GoPsiTypeStruct structTypePsi = typeStruct.getPsiType();
        if (structTypePsi == null)
            return;

        processStruct(structTypePsi);
    }

    private void processStruct(GoPsiTypeStruct psiTypeStruct) {

        for (GoTypeStructField subField : psiTypeStruct.getFields()) {
            for (GoLiteralIdentifier identifier : subField.getIdentifiers()) {
                if (ignore(identifier))
                    continue;

                addNamedField(identifier);
            }
        }

        for (GoTypeStructAnonymousField subField : psiTypeStruct.getAnonymousFields()) {
            if (!ignore(subField)) {
                addAnonymousField(subField);
                processAnonymousField(subField);
            }
        }
    }

    private void addAnonymousField(GoTypeStructAnonymousField field) {
        String name = field.getFieldName();
        List<GoTypeStructAnonymousField> fields = anonymousFieldsMap.get(name);
        if (fields == null) {
            fields = new ArrayList<GoTypeStructAnonymousField>();
            anonymousFieldsMap.put(name, fields);
            ignoreNames.add(name);
        }

        fields.add(field);
    }

    private void addNamedField(GoLiteralIdentifier identifier) {
        if (identifier.isBlank()) return;

        String name = identifier.getName();
        List<GoLiteralIdentifier> fields = namedFieldsMap.get(name);
        if (fields == null) {
            fields = new ArrayList<GoLiteralIdentifier>();
            namedFieldsMap.put(name, fields);
        }

        fields.add(identifier);
    }

    private Set<String> getDirectFieldNameSet() {
        Set<String> fields = new HashSet<String>();
        for (GoTypeStructField field : struct.getFields()) {
            for (GoLiteralIdentifier identifier : field.getIdentifiers()) {
                if (!identifier.isBlank()) {
                    fields.add(identifier.getName());
                }
            }
        }

        return fields;
    }
}
