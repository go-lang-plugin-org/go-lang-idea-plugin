package ro.redeul.google.go.lang.psi.typing;

public class GoFunctions {

    public static Builtin getFunction(String name) {
        if (name == null)
            return Builtin.None;

        for (Builtin builtinFunc : Builtin.values()) {
            if (builtinFunc.name().toLowerCase().equals(name))
                return builtinFunc;
        }

        return Builtin.None;
    }

    public enum Builtin {
        None,

        Close,          // closing a channel
        Len, Cap,       // collection size and cap len(s) int, cap(s) int

        New,            // create an object <new(T) T>
        Make,           // create a slice / map / channel   <make(T, ..) T>

        Append,         // add to a slice
        Copy,
        Delete,

        Complex, Real, Imag,
        Panic,
        Recover,

        Print, Println
    }

}
