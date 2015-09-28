package main;
import "fmt"
type WaitGroup struct {
    counter int32; waiters int32; sema *uint32
}
func main() { fmt.Println(WaitGroup{sema:nil}) }
func <warning>main3</warning>() { fmt.Println(WaitGroup{<error>s1ema</error>:nil}) }
func <warning>main2</warning>() { fmt.Println(<error>WaitGrou1p</error>{<error>sema</error>:nil}) }

func _() {
	type policyTableEntry struct {
		Prefix     string
		Precedence uint8
		Label      uint8
	}

	type policyTable []policyTableEntry

	var _ = policyTable{
		{
			Prefix:     "test",
			Precedence: 50,
			Label:      0,
		},
		{
			Prefix:     "test2",
			Precedence: 40,
			Label:      1,
		},
	}
}
