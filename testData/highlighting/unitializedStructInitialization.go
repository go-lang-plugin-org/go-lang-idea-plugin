package demo2094

type demo struct {
	a string
	b int32
	c string
}

type bemo struct {
	demo
	x string
}

func _() (struct {
	x string
}) {
	_ = struct{ x string }{x: "demo"}
	_ = demo{<weak_warning descr="Unnamed field initialization">"demo"</weak_warning>}
	b, _ := demo{<weak_warning descr="Unnamed field initialization">"demo"</weak_warning>}, 1
	_ = demo{
		<weak_warning descr="Unnamed field initialization">"demo"</weak_warning>,
		<weak_warning descr="Unnamed field initialization">1</weak_warning>,
		<weak_warning descr="Unnamed field initialization">"demo"</weak_warning>,
	}
	_ = demo{a: "demo"}
	_ = demo{a: "demo", b: 1}
	_ = demo{
		a: "demo",
		<weak_warning descr="Unnamed field initialization">1</weak_warning>,
	}
	_ = bemo{x: "demo"}
	_ = b
	return struct{x string}{<weak_warning descr="Unnamed field initialization">"demo"</weak_warning>}
}
