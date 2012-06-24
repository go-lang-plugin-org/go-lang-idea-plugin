package ro.redeul.google.go.lang.psi.types.underlying;

public class GoUnderlyingTypeSlice implements GoUnderlyingType{
    private GoUnderlyingType memberType;

    public GoUnderlyingTypeSlice(GoUnderlyingType memberType) {
        this.memberType = memberType;
    }

    @Override
    public boolean isIdentical(GoUnderlyingType other) {
        if (other instanceof GoUnderlyingTypeSlice) {
            GoUnderlyingTypeSlice otherSlice =
                (GoUnderlyingTypeSlice)other;

            return memberType.isIdentical(otherSlice.memberType);
        }

        return false;
    }

    @Override
    public String toString() {
        return String.format("[]%s", memberType.toString());
    }
}
