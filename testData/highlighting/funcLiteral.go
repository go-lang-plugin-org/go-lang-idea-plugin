package main
import "fmt"

type Mutex struct {
    state int32
    sema  uint32
}

var a = func() Mutex {
    return Mutex{}
}()

func main() {
    fmt.Println(a.sema)
}