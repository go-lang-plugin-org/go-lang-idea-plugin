package main

type data struct {
	name string
}

func main() {
	m := map[string]data{"x": {"one"}}
	<error descr="cannot assign to m[\"x\"].name">m["x"].name</error> = "two"
	n := []data{{"one"}}
	n[0].name = "two"
	p := [1]data{{"one"}}
	p[0].name = "two"
}
