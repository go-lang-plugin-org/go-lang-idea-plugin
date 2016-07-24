package foo

import "io"

func _() {
	_ = io.LimitedReader{
		R: nil,
	}
}