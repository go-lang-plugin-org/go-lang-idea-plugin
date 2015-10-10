package foo

type de struct <fold text='{...}'>{
	field1 []int
	field2 string
	field3 int
}</fold>

func demo() <fold text='{...}'>{
	d := de<fold text='{...}'>{
		field1: []int<fold text='{...}'>{1, 2, 3}</fold>,
		field2: "string",
		field3: 1,
	}</fold>
	_ = d
}</fold>
