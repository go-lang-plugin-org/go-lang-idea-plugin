package foo

import "io"

func _() {
	_ = io.LimitedReader{
		<weak_warning descr="Unnamed field initialization"><caret>nil</weak_warning>,
	}
}