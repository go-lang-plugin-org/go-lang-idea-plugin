package main
import "fmt"
import "strconv"

func main() {
    p := fmt.Printf
    ((fmt.Printf))("Hello\n", 1)
    (fmt.Printf)("Hello\n", 1)
    p("Hello\n", 1)

    j := interface{}(1)
    if (false) {p("", j); return }

    var i int
    f := ((fmt.Scanf))
    f("%d", &i)
    str := strconv.FormatInt(int64(i), 10)
    hex, _ := strconv.ParseInt(str, 16, 64)
    fmt.Printf("%d\n", hex)
}