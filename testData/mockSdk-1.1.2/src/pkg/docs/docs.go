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

// Global comment
var (
	// InnerVariable_1
	InnerVariable_1,
	// InnerVariable_2 
	InnerVariable_2,
	// InnerVariable_3 
	InnerVariable_3 int
	// InnerVariable_4
	InnerVariable_4 int
	InnerVariable_5 int
)