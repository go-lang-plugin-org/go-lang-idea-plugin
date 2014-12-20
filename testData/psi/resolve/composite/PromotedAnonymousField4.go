package main

type T1 struct {
	value int
}

type T2 struct {
	T1
}

type T3 struct {
	/*def*/T2
}

type T4 struct {
	T3
}

func foo(t4 T4) {
    t4./*ref*/T2
}
