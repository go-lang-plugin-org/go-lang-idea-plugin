package a

type FooBar string

const (
	x FooBar = "something"
)

func foo(FooBar) {}

func _() {
	foo(x<caret>)
}