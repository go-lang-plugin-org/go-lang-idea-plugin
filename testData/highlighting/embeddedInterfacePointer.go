package demo

type (
	demo interface {}
	demo2 interface {}
	dem struct {
		<error descr="Embedded type cannot be a pointer to interface">*demo</error>
		demo2
	}
)
