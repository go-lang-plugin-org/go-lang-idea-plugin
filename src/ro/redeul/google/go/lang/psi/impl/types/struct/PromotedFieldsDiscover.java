package ro.redeul.google.go.lang.psi.impl.types.struct;

import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeStruct;
import ro.redeul.google.go.lang.psi.types.GoStructPromotedFields;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructAnonymousField;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;
import ro.redeul.google.go.lang.psi.typing.GoTypeStruct;
import ro.redeul.google.go.lang.psi.typing.GoTypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PromotedFieldsDiscover {
    Map<String, List<GoLiteralIdentifier>> namedFieldsMap = new HashMap<String, List<GoLiteralIdentifier>>();
    Map<String, List<GoTypeStructAnonymousField>> anonymousFieldsMap = new HashMap<String, List<GoTypeStructAnonymousField>>();

    GoPsiTypeStruct struct;
    Set<String> ignoreNames;

    public PromotedFieldsDiscover(GoPsiTypeStruct struct) {
        this(struct, Collections.<String>emptySet());
    }

    public PromotedFieldsDiscover(GoPsiTypeStruct struct, Set<String> ignoreNames) {
        this.struct = struct;
        this.ignoreNames = new HashSet<String>(ignoreNames);
        this.ignoreNames.addAll(getDirectFieldNameSet());
    }

    public GoStructPromotedFields getPromotedFields() {
        discover();
        return new GoStructPromotedFields(getNamedFields(), getAnonymousFields());
    }

    private boolean ignore(GoTypeStructAnonymousField field) {
        return ignoreNames.contains(field.getFieldName());
    }

    private boolean ignore(GoLiteralIdentifier identifier) {
        return ignoreNames.contains(identifier.getUnqualifiedName());
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
            GoTypeStruct struct = GoTypes.resolveToStruct(field.getType());
            if (struct == null) {
                continue;
            }

            GoPsiTypeStruct psiType = struct.getPsiType();
            if (psiType == null) {
                continue;
            }

            for (GoTypeStructField subField : psiType.getFields()) {
                for (GoLiteralIdentifier identifier : subField.getIdentifiers()) {
                    if (!ignore(identifier)) {
                        addNamedField(identifier);
                    }
                }
            }

            for (GoTypeStructAnonymousField subField : psiType.getAnonymousFields()) {
                if (ignore(subField)) {
                    continue;
                }

                addAnonymousField(subField);
                GoTypeStruct subStruct = GoTypes.resolveToStruct(subField.getType());
                if (subStruct == null) {
                    continue;
                }

                GoPsiTypeStruct subPsiType = struct.getPsiType();
                if (subPsiType != null) {
                    discoverSubType(subPsiType);
                }
            }
        }
    }

    private void discoverSubType(GoPsiTypeStruct subPsiType) {
        GoStructPromotedFields fields = new PromotedFieldsDiscover(subPsiType, ignoreNames).getPromotedFields();
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

        String name = identifier.getUnqualifiedName();
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
                    directFields.add(identifier.getUnqualifiedName());
                }
            }
        }

        for (GoTypeStructAnonymousField field : struct.getAnonymousFields()) {
            directFields.add(field.getFieldName());
        }
        return directFields;
    }

}
