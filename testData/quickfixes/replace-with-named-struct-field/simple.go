package foo

func _() {
    _ = struct {
        x string
    }{
        <caret>"string",
    }
}