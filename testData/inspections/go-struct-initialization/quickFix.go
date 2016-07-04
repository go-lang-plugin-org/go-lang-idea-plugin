package foo

func _() {
    _ = struct {
        x string
    }{
        <weak_warning descr="Unnamed field initialization"><caret>"string"</weak_warning>,
    }
}