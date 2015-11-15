package docs

// TypeResult func comment
func TypeResult(s string) string {
    return s
}

// MultiType is a function like all other functions
func MultiType(
        demo interface{},
        err error,
    ) (
    []interface{},
    error,
    ) {
    return []interface{}{}, err
}