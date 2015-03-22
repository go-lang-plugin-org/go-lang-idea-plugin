package main;
import "fmt"
type WaitGroup struct {
    counter int32; waiters int32; sema *uint32
}
func main() { fmt.Println(WaitGroup{sema:nil}) }
func <warning>main3</warning>() { fmt.Println(WaitGroup{<error>s1ema</error>:nil}) }
func <warning>main2</warning>() { fmt.Println(<error>WaitGrou1p</error>{<error>sema</error>:nil}) }