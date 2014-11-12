package ro.redeul.google.go.lang.psi.impl.types.struct;

import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeStruct;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructAnonymousField;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructPromotedFields;
import ro.redeul.google.go.lang.psi.typing.*;

import java.util.*;

public class PromotedFieldsDiscover {
    private final Map<String, List<GoLiteralIdentifier>> namedFieldsMap = new HashMap<String, List<GoLiteralIdentifier>>();
    private final Map<String, List<GoTypeStructAnonymousField>> anonymousFieldsMap = new HashMap<String, List<GoTypeStructAnonymousField>>();

    private final GoPsiTypeStruct struct;
    private final Set<String> ignoreNames;

    public PromotedFieldsDiscover(GoPsiTypeStruct struct) {
        this(struct, Collections.<String>emptySet());
    }

    private PromotedFieldsDiscover(GoPsiTypeStruct struct, Set<String> ignoreNames) {
        this.struct = struct;
        this.ignoreNames = new HashSet<String>(ignoreNames);
        this.ignoreNames.addAll(getDirectFieldNameSet());
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
            if (identifiers.size() == 1 && !ignore(identifiers.get(0))) {
                namedFields.add(identifiers.get(0));
            }
        }

        return namedFields.toArray(new GoLiteralIdentifier[namedFields.size()]);
    }

    private GoTypeStructAnonymousField[] getAnonymousFields() {
        List<GoTypeStructAnonymousField> anonymousFields = new ArrayList<GoTypeStructAnonymousField>();
        for (List<GoTypeStructAnonymousField> fields : anonymousFieldsMap.values()) {
            if (fields.size() == 1 && !ignore(fields.get(0))) {
                anonymousFields.add(fields.get(0));
            }
        }
        return anonymousFields.toArray(new GoTypeStructAnonymousField[anonymousFields.size()]);
    }

    private void discover() {
        namedFieldsMap.clear();
        anonymousFieldsMap.clear();

        for (GoTypeStructAnonymousField field : struct.getAnonymousFields()) {
            processAnonymousField(field);
        }
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
        discoverSubType(structTypePsi);
    }

    private void processStruct(GoPsiTypeStruct psiTypeStruct) {

        for (GoTypeStructField subField : psiTypeStruct.getFields()) {
            for (GoLiteralIdentifier identifier : subField.getIdentifiers()) {
                if (!ignore(identifier)) {
                    addNamedField(identifier);
                }
            }
        }

        for (GoTypeStructAnonymousField subField : psiTypeStruct.getAnonymousFields()) {
            if (ignore(subField)) {
                continue;
            }

            addAnonymousField(subField);
        }
    }

    private void discoverSubType(GoPsiTypeStruct subPsiType) {
        GoTypeStructPromotedFields fields = new PromotedFieldsDiscover(subPsiType, ignoreNames).getPromotedFields();
        for (GoLiteralIdentifier identifier : fields.getNamedFields()) {
            addNamedField(identifier);
        }

        for (GoTypeStructAnonymousField field2 : fields.getAnonymousFields()) {
            addAnonymousField(field2);
        }
    }

    private void addAnonymousField(GoTypeStructAnonymousField field) {
        String name = field.getFieldName();
        List<GoTypeStructAnonymousField> fields = anonymousFieldsMap.get(name);
        if (fields == null) {
            fields = new ArrayList<GoTypeStructAnonymousField>();
            anonymousFieldsMap.put(name, fields);
        }
        fields.add(field);
    }

    private void addNamedField(GoLiteralIdentifier identifier) {
        if (identifier.isBlank()) {
            return;
        }

        String name = identifier.getName();
        List<GoLiteralIdentifier> fields = namedFieldsMap.get(name);
        if (fields == null) {
            fields = new ArrayList<GoLiteralIdentifier>();
            namedFieldsMap.put(name, fields);
        }
        fields.add(identifier);
    }

    private Set<String> getDirectFieldNameSet() {
        Set<String> directFields = new HashSet<String>();
        for (GoTypeStructField field : struct.getFields()) {
            for (GoLiteralIdentifier identifier : field.getIdentifiers()) {
                if (!identifier.isBlank()) {
                    directFields.add(identifier.getName());
                }
            }
        }

        for (GoTypeStructAnonymousField field : struct.getAnonymousFields()) {
            directFields.add(field.getFieldName());
        }
        return directFields;
    }

}
