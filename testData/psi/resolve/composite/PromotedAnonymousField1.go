package main

type T1 struct {
	/*def*/intValue int
}

type T2 struct {
	T1
}

func foo(t2 T2) {
    t2./*ref*/intValue
}
