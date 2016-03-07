package main

type S struct {
	/*def*/a int
}

func main() {
	_ = map[string][]S{
		"key": {{/*ref*/a: 5}},
	}
}