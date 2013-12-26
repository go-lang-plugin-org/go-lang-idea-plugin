package main

import "fmt"

func main() {
println    (  1,2,3)
fmt.Println   (  1,    2,    3)

println(  1,
2,
3)

println(
1,
2,
3)

fmt.Println(1,
2,
3)

fmt.Println(
1,
2,
(1+     // all space but one before comment should be removed
2*-  3),
3,
)

fmt.Println(

)

make([]*X,0,n)

func  (  )  {
println()
}()

go    func   (  )  {
println()
}  (  )

{
a:=5
result, err := demoCall(&DemoStruct{
a:        true,
b: false,
cd:  false,
}, nil)
}
}

-----
package main

import "fmt"

func main() {
	println(1, 2, 3)
	fmt.Println(1, 2, 3)

	println(1,
		2,
		3)

	println(
		1,
		2,
		3)

	fmt.Println(1,
		2,
		3)

	fmt.Println(
		1,
		2,
		(1 + // all space but one before comment should be removed
			2*-3),
		3,
	)

	fmt.Println()

	make([]*X, 0, n)

	func() {
		println()
	}()

	go func() {
		println()
	}()

	{
		a := 5
		result, err := demoCall(&DemoStruct{
			a:  true,
			b:  false,
			cd: false,
		}, nil)
	}
}
