package mongo

import "fmt"

type Collection struct{}
type CollectionLoaderInterface interface {
    MangoCollection(string) *Collection
}

func <warning descr="Unused function 'Collection'">Collection</warning>(parent interface{},collection string) *Collection{
     switch parent := parent.(type) {
          case CollectionLoaderInterface:
          return parent.MangoCollection(collection)
     }
     return nil
}

func <warning>main1</warning>(err error) {
    switch err.(type) {
        case nil: return
    }
}

type advSearch struct {
    Genres struct {
        Generals  []string
        Thematics struct {
            Genres  []string
            Missing bool
        }
        Demographics struct {
            Genres  []string
            Missing bool
        }
    }
}

func <warning>search</warning>() bool {
    m := advSearch{}
    return m.Genres.Demographics.Missing
}


func g(a, b, _ int) (int, int) {
    return a, b
}

func f(_, _ int) {
}

func <warning>test</warning>() {
    f(g(1, 2, 3))
}

func <warning>test2</warning>() (bool, string) {
    ch := make(chan string)
    var str string
    var isOpen bool
    select {
    case str = <-ch :
    case str, isOpen = <-ch:
    case s, ok := <-ch:
        str = s
        isOpen = ok
    case s := <-ch:
        str = s
    }
    return isOpen, str
}


func <warning>Test23</warning>() (err error) {
    var c chan int
    select {
    case <error>err</error> := (<-c):
    }
    return err
}

func Demo() error {
    return fmt.Errorf("err %s", "a")
}

func <warning>main</warning>() {
    var err error

    switch  {
    case 1==2:
        err := Demo()
        panic(err)
    default:
        err = Demo()
        panic(err)
    }
    //panic(err)
}

func <warning>main2</warning>() {
    ch := make(chan string, 2)
    ch <- "first"
    ch <- "second"

    select {
    case a := <-ch:
        println(a)
    }

    var (
        a int // marked as unused variable `a`
    )

    select {
    case a = <-ch:
        println(a)
    }
    println(a)
}

type d struct{
    A string
}

func (a *d) Func() (*d, error) {
    return a, nil
}

func de(x string) *d {
    return &d{A: x}
}

func demo23(a *d, _ error) string {
    return a.A
}

func <warning>main23</warning>() {
    _ = demo23(de("1").Func())
}

func foo_m(_ int) {
}

func bar_m() (int, int) {
	return 0, 0
}

func <warning>main127</warning>() {
	foo_m<error>(bar_m())</error>
}

type AnInterface interface {
	MyMethod() error
}

type bar struct {}

func (*bar) MyMethod() error {
	return nil
}

type mystruct struct {}

func (x *mystruct) getAnInterface() AnInterface {
	return &bar{}
}

var getAnInterface = (*mystruct).getAnInterface

func _() {
	fmt.Println(getAnInterface(&mystruct{}).MyMethod())
}

type (
	funcTiOn func() ([]string, Cmd)

	Cmd interface {
		Ini(keys map[string]string)
	}
)

var (
	funcs = []funcTiOn{}
)

func _() {
 	for idx := range funcs {
		_, handler := funcs[idx]()
		handler.Ini(map[string]string{})
	}

	for _, val := range funcs {
		_, handler := val()
		handler.Ini(map[string]string{})
	}
}

type Decoder func(p int) (size int)

func (e Decoder) ConvertString(s string) string { return s }
func NewDecoder(_ string) Decoder { return nil }

func _() {
	dc := NewDecoder("GB18030")
	dc(1)
 	fmt.Println(dc.ConvertString("abc123"))
}

type dnsNameTest struct {
	name   string
	result bool
}

var dnsNameTests = []dnsNameTest{
	{"_xmpp-server._tcp.google.com", true},
}

func _() {
	for _, tc := range dnsNameTests {
		println(tc.name)
		println(tc.result)
	}

	// append loses return type
	dnsNameTests2 := append(dnsNameTests, []dnsNameTest{})
	for _, tc := range dnsNameTests2 {
		println(tc.name)
		println(tc.result)
	}
}