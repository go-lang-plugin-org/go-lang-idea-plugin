package main

type S struct {
	a int
}

func main() {
	_ = map[string]S{
		"key": {a: 5},
	}
	_ = map[string][]S{
		"key": {{a: 5}},
	}
	_ = map[string][][]S{
		"key": {{{a: 5}}},
	}
	_ = map[string][][][]S{
		"key": {{{{a: 5}}}},
	}
}