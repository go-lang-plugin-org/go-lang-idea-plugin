package exportedowndeclaration

const Hello1, hello2 = 1, 2

const Hello3, <weak_warning desc="Exported const 'Hello4' should have its own declaration">Hello4</weak_warning> = 3, 4

var Hello5, hello6 int

var Hello7, <weak_warning desc="Exported var 'Hello8' should have its own declaration">Hello8</weak_warning> int

var (
	Hello9, <weak_warning desc="Exported var 'Hello10' should have its own declaration">Hello10</weak_warning> int
	Hello11, <weak_warning desc="Exported var 'Hello12' should have its own declaration">Hello12</weak_warning> int
)

func _() {
	_, _, _, _ = Hello5, hello6, Hello7, Hello8
	_, _, _, _ = Hello9, Hello10, Hello11, Hello12
}
