package main

type data struct {
	name string
}

func _() {
	type foo struct{ Name string }

	bad := map[string]foo{}

	for key := range bad {
		<error descr="cannot assign to bad[key].Name">bad[key].Name</error> = bad[key].Name + " Updated"
	}

	good := map[string]*foo{}
	for key := range good {
		good[key].Name = good[key].Name + " Updated"
	}
}

func main() {
	m := map[string]data{"x": {"one"}}
	<error descr="cannot assign to m[\"x\"].name">m["x"].name</error> = "two"
	n := []data{{"one"}}
	n[0].name = "two"
	p := [1]data{{"one"}}
	p[0].name = "two"
}
