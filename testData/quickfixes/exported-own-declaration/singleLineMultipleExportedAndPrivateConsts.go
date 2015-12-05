package demo2049

const Hello1, hello2, Hell<caret>lllo, help = 1, 2, 3, 4 // This is valid

const Hello3, Hello4 = 3, 4 // exported const Hello4 should have its own declaration

var Hello5, hello6 int // This is valid

// exported var Hello8 should have its own declaration
var Hello7, Hello8 int // exported var Hello8 should have its own declaration

const (
	Foo            = 1
	Bar, Baz   int = 1, 2
	Bar2, Baz2     = 1, 2
)

var (
	Hello9, Hello10  int
	Hello11, Hello12 int
	Hello13, Hello14 = 1, 2
)

func _() {
	_, _, _, _ = Hello5, hello6, Hello7, Hello8
	_, _, _, _ = Hello9, Hello10, Hello11, Hello12
	_, _ = Hello13, Hello14
}
