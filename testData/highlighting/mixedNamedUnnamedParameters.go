package main

func _(<warning descr="Unused parameter 'int1'">int1</warning>, <warning descr="Unused parameter 'string1'">string1</warning>, <warning descr="Unused parameter 'a1'">a1</warning> error) (<warning descr="Unused named return parameter 'int'">int</warning>, <warning descr="Unused named return parameter 'string'">string</warning>, <warning descr="Unused named return parameter 'a'">a</warning> error) {
	return 1, "a", nil
}

func _(int, string, error) (int, string, error) {
	return 1, "a", nil
}

func _<error descr="Function has both named and unnamed parameters '(a1, b1 int, string, error)'">(a1, b1 int, string, error)</error> (<error descr="Unresolved type 'a'">a</error>, <error descr="Unresolved type 'b'">b</error>, int, string, error) {
	return 1, 2, "a", nil
}

type d int

func (d) _(int1, string1, a1 error) (<warning descr="Unused named return parameter 'int'">int</warning>, <warning descr="Unused named return parameter 'string'">string</warning>, <warning descr="Unused named return parameter 'a'">a</warning> error) {
	return 1, "a", nil
}

func (d) _(int, string, error) (int, string, error) {
	return 1, "a", nil
}

func (d) _<error descr="Method has both named and unnamed parameters '(a1, b1 int, string, error)'">(a1, b1 int, string, error)</error> <error descr="Method has both named and unnamed return parameters '(a, b int, string, error)'">(a, b int, string, error)</error> {
	return 1, 2, "a", nil
}

func main() {
	x := func(int1, string1, a1 error) (int, string, a error) {
		return 1, "a", nil
	}

	y := func(int, string, error) (int, string, error) {
		return 1, "a", nil
	}

	z := func<error descr="Closure has both named and unnamed parameters '(a1, b1 int, string, error)'">(a1, b1 int, string, error)</error> <error descr="Closure has both named and unnamed return parameters '(a, b int, string, error)'">(a, b int, string, error)</error> {
		return 1, 2, "a", nil
	}

	_, _, _ = x, y, z
}
