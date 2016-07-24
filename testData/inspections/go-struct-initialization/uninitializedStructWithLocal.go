package demo2094

import "io"
import "os"

type demo struct {
	a string
	b int32
	c string
}

type bemo struct {
	demo
	x string
}

type MyStruct struct {
	a string
	b []MyElem
	c [3]MyElem
	d map[string]MyElem
}

type MyElem struct {
	foo string
}

func _() struct {
	x string
} {
	// good defs
	_ = []MyStruct{
		{
			a: "bar",
			b: []MyElem{
				{foo: "foo"},
				{foo: "foo"},
				{foo: "foo"},
			},
			c: [3]MyElem{
				{foo: "foo"},
				{foo: "foo"},
				{foo: "foo"},
			},
			d: map[string]MyElem{
				"foo": {foo: "foo"},
				"bar": {foo: "foo"},
				"baz": {foo: "foo"},
			},
		},
	}

	_ = map[string]MyStruct{
		"foo": {
			a: "bar",
			b: []MyElem{
				{foo: "foo"},
				{foo: "foo"},
				{foo: "foo"},
			},
			c: [3]MyElem{
				{foo: "foo"},
				{foo: "foo"},
				{foo: "foo"},
			},
			d: map[string]MyElem{
				"foo": {foo: "foo"},
				"bar": {foo: "foo"},
				"baz": {foo: "foo"},
			},
		},
	}

	_ = struct{ x string }{x: "demo"}
	_ = demo{a: "demo"}
	_ = demo{a: "demo", b: 1}

	// bad defs
	_ = demo{<weak_warning descr="Unnamed field initialization">"demo"</weak_warning>}
	b, _ := demo{<weak_warning descr="Unnamed field initialization">"demo"</weak_warning>}, 1
	_ = demo{
		<weak_warning descr="Unnamed field initialization">"demo"</weak_warning>,
		<weak_warning descr="Unnamed field initialization">1</weak_warning>,
		<weak_warning descr="Unnamed field initialization">"demo"</weak_warning>,
	}

	_ = demo{
		a: "demo",
		<weak_warning descr="Unnamed field initialization">1</weak_warning>,
	}
	_ = bemo{x: "demo"}
	_ = b
	return struct{ x string }{<weak_warning descr="Unnamed field initialization">"demo"</weak_warning>}
}

type Item struct {
	Name string
}

type Menu []Item

func _() {
	// good defs
	_ = Menu{
		{Name: "home"},
	}

	_ = os.LinkError{
		Op:  "string",
		Old: "string",
		New: "string",
		Err: nil,
	}

	// bad defs
	_ = io.LimitedReader{
		<weak_warning descr="Unnamed field initialization">nil</weak_warning>,
	}
	_ = os.LinkError{
		<weak_warning descr="Unnamed field initialization">"string"</weak_warning>,
		<weak_warning descr="Unnamed field initialization">"string"</weak_warning>,
		<weak_warning descr="Unnamed field initialization">"string"</weak_warning>,
		<weak_warning descr="Unnamed field initialization">nil</weak_warning>,
	}
}

type assertion struct {
	query string
}

func _() {
	tests := []struct {
		assertions []assertion
	}{
		{
			assertions: []assertion{
				{
					query: "foo",
				},
			},
		},
	}

	_ = tests
}
