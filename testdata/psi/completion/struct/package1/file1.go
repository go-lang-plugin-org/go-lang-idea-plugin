package package1;

type T1 struct {
	B int // should appear as prompted
	c int // should NOT appear as promoted
}

type x int

type DemoStruct struct {
	T1  // should appear as anonymous
	x   // should NOT appear as anonymous
	A int // should appear as a field
	b string // should not appear as a field
}
