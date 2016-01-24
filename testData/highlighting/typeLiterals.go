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

type Response struct { ResponseWriter }
type ResponseWriter interface { Header() Header }
type Header int

func (h Header) Add(_, _ string) { }

func (r Response) AddHeader(header string, value string) Response {
	rr := r.Header()
	rr.Add("", "")
	r.Header().Add(header, value)
	return r
}

type Foo struct {
	A int
}

type Zoo Foo
type Bar Zoo

func _() {
	fmt.Println(Test())
}

func Test() Bar {
	fmt.Println(Foo{A:1})
	fmt.Println(Zoo{A:1})
	return Bar{A: 1}
}

type setAuthRequest auth
type auth struct {
	Type   int32
	Scheme string
	Auth   []byte
}

func _(scheme string, auth []byte) *setAuthRequest {
	return &setAuthRequest{Type: 0, Scheme: scheme, Auth: auth}
}