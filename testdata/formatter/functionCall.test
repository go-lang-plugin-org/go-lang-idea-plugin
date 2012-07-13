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
(1+     // space before comment shouldn't be removed
2*-  3),
3,
)

fmt.Println(

)

func  (  )  {
println()
}()

go    func   (  )  {
println()
}  (  )

{
a:=5
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
        (1 +     // space before comment shouldn't be removed
            2*-3),
        3,
    )

    fmt.Println()

    func() {
        println()
    }()

    go func() {
        println()
    }()

    {
        a := 5
    }
}
