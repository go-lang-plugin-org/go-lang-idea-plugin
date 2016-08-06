package main

import "<error descr="Cannot resolve file ''"></error>"
import fmt "<error descr="Cannot resolve file ''"></error>"
import "net/http"
import "io"

func x(string) {}

func  main() {
    println([]string<error descr="'!=', '%', '&', '&^', '(', ')', '*', '+', ',', '-', '.', '...', '/', <, <<, <=, '==', '>', '>=', '>>', '^', '{' or '|' expected, got ')'">)</error>
    ((<error descr="Type string is not an expression">string</error>))
    x((string)("foo"))
    x(<error descr="Type string is not an expression">string</error> + <error descr="Type string is not an expression">string</error>)
    var b Boom
    Boom.Run(b, aaa{})
    <error descr="Type string is not an expression">string</error>
	test := <error descr="Unresolved reference 'test'">test</error>
	Println(test)
	test.<EOLError descr="'!=', '%', '&', '&^', '(', '*', '+', '++', '-', '--', '/', <, <<, <=, <expression>, '==', '>', '>=', '>>', '^', identifier or '|' expected, got '}'"></EOLError>
}

type aaa aaa

type Boom struct {
   err aaa
}

func (b *Boom) Run(a aaa) (r1 aaa, r2 aaa) {
   b.err + a + r1 + r2
<error descr="Missing return at end of function">}</error>

func _() int {
Label:
	goto Label
}

func _() int {
	for i := 1; ; i++ {
		break
	}
<error descr="Missing return at end of function">}</error>

func _() int {
	Label:
	for i := 1; ; i++ {
		break Label
	}
<error descr="Missing return at end of function">}</error>

func _() int {
	Label:
	for i := 1; ; i++ {
		if (true ) {
			break Label
		}
 	}
<error descr="Missing return at end of function">}</error>

func _() int {
	Label:
	for i := 1; ; i++ {
		goto Label
 	}
}


func _() int {
	for true {
		return 1
	}
<error descr="Missing return at end of function">}</error>

func _() int {
	for i := 1; ; i++ {
	}
}

func _() int {
	for i := 1; i < 10; i++ {
		return 1
	}
<error descr="Missing return at end of function">}</error>

func <error descr="Duplicate function name">foo</error>() {
    i := 1
    for (i) {return 0}
    if (i) {return <error descr="Unresolved reference 'j'">j</error>}

    headers := []int{1}
    for _, h := range headers {
      h++
    }
}

var nil int

type <warning descr="Type 'int' collides with builtin type">int</warning> int
type <warning descr="Type 'byte' collides with builtin type">byte</warning> byte
type <warning descr="Type 'bool' collides with builtin type">bool</warning> bool
type <warning descr="Type 'float32' collides with builtin type">float32</warning> float32
type <warning descr="Type 'string' collides with builtin type">string</warning> string

type T struct {
	a int
}
func (tv  T) Mv(int) int         { return 0 }  // value receiver
func (tp *T) Mp(float32) float32 { return 1 }  // pointer receiver

var t T

func _() {
    t.Mv(7)
    T.Mv(t, 7)
    (T).Mv(t, 7)
    f1 := T.Mv; f1(t, 7)
    f2 := (T).Mv; f2(t, 7)
    <error descr="Use of package fmt without selector">fmt</error>
    f1(<error descr="Use of package fmt without selector">fmt</error>)
}

func <error descr="Duplicate function name">foo</error>() {
    a := &A{}
    b := &B{b:"bbb"}
    e := &Empty{}
    y := make(<error descr="Cannot make A">A</error>, 10)
    z := new(A)

    y.hola()
    z.hola()

    a.hola()
    b.hola()
    e.hola()
    b.b = "jj"
}

type B struct {
    *A
    b string
}
type A struct {
    *Empty
    a int
}
type Empty struct {
}
func (this *Empty) hola() {
}

type AA struct {
    N int
}
func _(b *AA) {
     b.N
}

func _(interface{}, ...interface{}) {
}

func _(o interface{}) {
  func(i interface{}) {
    Println(o)
    Println(i)
  }
}

func _(integers []int) []int {
  ch := make(chan int)
  <error descr="Unused variable 'responses'">responses</error> := []int{}
  for _, <error descr="Unused variable 'i'">i</error> := range integers {
      go func(j int) {
          ch <- j * j
      }(<error descr="Unresolved reference 'j'">j</error>)
  }
  for _, i := range integers {
      go func(j int) {
          ch <- j * j
      }(i)
  }
  <error descr="Unused variable 'err'">err</error> := 1
  _, err = 1, 1
  return integers
}

func Println(...interface{})  {
}

func _() {
	type connError struct {
		cn  int
	}
	ch := make(chan connError)
	Println(ch.<error descr="Unresolved reference 'cn'">cn</error>)
}

type ConnStatus interface {
	Status() int32
	Error() error
}

func Bind() <-chan ConnStatus {
	ch := make(chan ConnStatus, 1)
	return ch
}

func _() {
	a := Bind()
	for c := range a {
		Println("Connection status:", c.Status())
	}
}

type Iface interface {
  Boo() int
}

const name1 int = 10

func _(st interface {Foo()}, st1 Iface) {
    <error descr="Cannot assign to constant">name1</error>, <error descr="Cannot assign to constant">name1</error> = 1, 2
    for <error descr="Cannot assign to constant">name1</error> = range <error descr="Cannot range over data (type interface {...})">st</error> {
        
    }
    for <error descr="Cannot assign to constant">name1</error> = range <error descr="Cannot range over data (type interface {...})">st</error> {
        
    }
    Println(<error descr="st.Foo() used as value">st.Foo()</error> + st1.Boo())
}

// No new variables on left side
func _() {
	c := make(chan int)
	
	select {
	case <error descr="No new variables on left side of :=">_</error> := <-c:
		println("Ololo")
	default:
	}
	for <error descr="No new variables on left side of :=">_</error> := range "asd"  {
	}
	if <error descr="_ := 1 used as value"><error descr="No new variables on left side of :=">_</error> := 1</error> {
		return
	}
	
	buzz := 0
	println(buzz)

	<error descr="No new variables on left side of :=">buzz</error> := <-c
	buzz = <-c
}

func _() { goto Label1; Label1: 1; goto <error descr="Unresolved label 'Label2'">Label2</error>}

type compositeA struct { int }
type compositeB struct { byte }

func _ () {
	a0, b0 := composite1()
	Println(a0.int, b0.byte)
	a1, b1 := new(compositeA), new(compositeB)
	Println(a1.int, b1.byte)
	a2, b2 := composite2()
	Println(a2.int, b2.byte)
}

func composite1() (*compositeA, *compositeB) {
	return new(compositeA), new(compositeB)
}
func composite2() (*compositeA, *compositeB) {
	return new(compositeA), new(compositeB)
}

func _(o interface {test1() int}) {
	Println(o.test1())
}

func _() (int) {
	 type connError struct { err int }
	ch := make(chan connError)
  select {
		case ce  := <-ch:
		return ce.err
	}
}

type Item struct {
	Key string
	Value []byte
}

func _() {
	m := GetMulti()
	v := m["AA"].Value
	Println(v)
	Println(GetMulti()["AA"].Key)
}

func GetMulti() (map[string]*Item) {
	m := make(map[string]*Item)
	m["AA"] = &Item{}
	return m
}

type WebService struct { rootPath string }
func (w *WebService) Path(string) *WebService { return w }
func (w *WebService) GET(string) *RouteBuilder { return new(RouteBuilder) }
type RouteBuilder struct { bool }
func (w *WebService) Route(*RouteBuilder) *WebService { return w }
func _() {
	ws1 := new(WebService).Path("/")
	ws1.GET("").bool
	ws1.Route(ws1.GET("/{type}/{id}"))
}

type ServiceError struct {
	Code    int
	Message string
}

type ForSwitch struct {
	Value interface{}
}

func _(x ForSwitch) {
	switch <error descr="Unused variable 'x'">x</error> := x.Value.(type) {
	}
}

func _() {
  err := nil
  switch err.(type) {
    case ServiceError:
            ser := <error descr="Invalid type assertion: err.(ServiceError), (non-interface type int on left)">err</error>.(ServiceError)
            Println(ser.Code)
            Println([]byte(ser.Message))
    }
}

func _(err interface{}) {
	switch err.(type) {
	case ServiceError:
		ser := err.(ServiceError)
		Println(ser.Code)
		Println([]byte(ser.Message))
	}
}

type Request struct {
    Request *http.Request
}

func _(r *Request)  {
    r.Request.URL
}

type ReadCloser interface {
    io.Reader
    io.Closer
}

func _(b ReadCloser)  {
    b.<error descr="Unresolved reference 'Closer'">Closer</error>()
    b.Close()
}

func _() {
var tempregexs = []struct {
    template, regex         string
    literalCount, varCount int
}{
    {"", "^(/.*)?$", 0, 0},
    {"/a/{b}/c/", "^/a/([^/]+?)/c(/.*)?$", 2, 1},
    {"/{a}/{b}/{c-d-e}/", "^/([^/]+?)/([^/]+?)/([^/]+?)(/.*)?$", 0, 3},
    {"/{p}/abcde", "^/([^/]+?)/abcde(/.*)?$", 5, 1},
}
    for <error descr="Unused variable 'i'">i</error>, fixture := range tempregexs {
          fixture.regex
    }
}

type Route struct {
}

func (r Route) matchesContentType() {
}

func _() {
    rs := []Route{}
    for _, each := range rs {
        if each.matchesContentType() {
        }
    }
    var rs2, <error descr="Unused variable 'i'">i</error> = makeTuple()
    for _, each := range rs2 {
        if each.matchesContentType() {
        }
    }
}

func makeTuple() ([]Route, int) {
  return []Route{}, 1
}

type Greeting struct {
    Author  string
    Content string
}

func _() {
    greetings := make([]Greeting, 0, 10)
    for i, value := range greetings {
        if (value.Content == "") {
            greetings[i].Content = "<Empty>"
        }
    }
}

type R struct {
    Body int 
}

func _() {
    response := make(chan *R, 1)
    r := <- response
    Println(r.Body)
    Println(response.<error descr="Unresolved reference 'Body'">Body</error>)
}

func _(<warning descr="Unused parameter 'a'">a</warning> int, <error descr="Duplicate argument 'a'">a</error> int, <warning descr="Unused parameter 'c'">c</warning>, <warning descr="Unused parameter 'd'">d</warning>, <error descr="Duplicate argument 'c'">c</error> int) (<error descr="Duplicate argument 'a'">a</error>, <error descr="Duplicate argument 'd'">d</error>, <warning descr="Unused named return parameter 'x'">x</warning> int) {
  return 1,1,1
}

func _(int,  <error descr="Can only use '...' as final argument in list">...</error> int, string) (_,_,_ int) {
    return 1,1,1
}

func _(_ int, _, _<error descr="Can only use '...' as final argument in list">...</error> int) (_,_,_ <error descr="Cannot use '...' in output argument list">...</error>int) {
    return 1,1,1
}

type RGBA struct {
    R, G, B, A uint8
}

type (
    Color RGBA
)

func _(col Color) string {
    Println(col.B)  
    
    var testdata *struct {
        a *[7]int
    }
    Println(testdata.a)
<error descr="Missing return at end of function">}</error>

func _(col Color) () {
    Println(col.B)  
    
    var testdata *struct {
        a *[7]int
    }
    Println(testdata.a)
}

type Name struct {
}

type B Name

func (self *Name) Foo(int) {
}

func _(b B, c Color) {
	b.<error descr="Unresolved reference 'Foo'">Foo</error>(1)
	c.A
}

var <warning descr="Unused variable 'Name11'">Name11</warning> string  = ""
var <warning descr="Unused variable 'nmame11'">nmame11</warning> string  = ""

func _() int {
      y, z := 1,3
      if y == z {}  // Just to avoid unused variable error
      {
              y, z := 1, 2 // Should not be an error for vars y and z shadow y and z from the outer scope
              if y == z {}  // Just to avoid unused variable error
      }
      return 1
}

func init() {
    
}

func _() int {
    {
        return 1
    }
}

func _(<warning descr="Unused parameter 'bar'">bar</warning> func(baz    int)) {
      <error descr="Unresolved reference 'baz'">baz</error>
}

type Ormer interface {
	Insert(interface{})
}

func _(o Ormer) {
	(*o).Insert(1)
}

type Conn interface {
	Do(commandName string, args ...interface{}) (reply interface{}, err error)
}

func String(interface{}, error) {
}
 
func _(c Conn) {
	String(c.Do("GET", "somekey"))
}

type MyType string

func (t MyType) Get(string) string { return "hello" }

func _() {
	st := MyType("tag")
	st.Get("key") // <- unresolved Get
}

type TestStruct struct {
	SomeId int
}

type DataSlice []*TestStruct

func NewDataSlice() *DataSlice {
	return &DataSlice{}
}

func _() {
	data := NewDataSlice()
	for _, element := range <error descr="Cannot range over data (type *DataSlice)">data</error> {
		if  element.SomeId > 20 {
			println("some text")
		}
	}
}

type Params struct { }

type Image interface { }

type ServerFunc func(params Params) (*Image, error)

func (f ServerFunc) Get(params Params) (*Image, error) {
	return f(params)
}

type Server interface {
	Get(Params) (*Image, error)
}

func _() {
	server := ServerFunc(func(Params) (*Image, error) {
		return nil, nil
	})
	server.Get(Params{})
}

func _() {
	type (
		client struct {
			message chan string
		}
		clientList struct {
			m (((map[string]*client)))
		}
	)
	cl := clientList{m: make(map[string]*client)}
	message := ""
	for _, c := range cl.m {
		c.message <- message
	}
}

func _() {
	addr := "test"
	x := struct {
		addr string
	}{addr: addr}
	Println(x)
}

func _() {
	tests:=[]struct{ want int}{}
	println(tests)
	want := ""
	Println(want)
}

type SensorFactory struct {
	Sensors map[string]string
}

func _() *SensorFactory {
	factory := new (SensorFactory)
	factory.Sensors = make(map[string]string)
	return factory
}

type Conn1 interface {
	Close() error
}

func _() {
	clientList := make(map[int]struct{
		message chan string
		conn *Conn1
	})
	message := ""
	for _, c := range clientList {
		select {
		case c.message <- message:
		default:
			(*c.conn).Close()
		}
	}
}

type inner_t struct{}
func (aa inner_t) foo() { }

func _() {
	c := 10
	var fun = func(c inner_t) {
		c.foo();
	}
	fun(inner_t{})
	print(c)
}

type B1 struct {a string}
type B2 B1

func (b B2) method() B2 {
	return B2{}
}

func _() {
	b := B2{a:""}
 	b.method()
}

func z(int) {}

func _() {
    var f float64
    f = 12.345
    var i int
    i = int(f)
    z(i)
    i = <error descr="Missing argument to conversion to int: int().">int()</error>
    z(i)
    i = <error descr="Too many arguments to conversion to int: int(3, 4).">int(3, 4)</error>
}
type myIFace string

func (myIFace) Boo() int {
    return 444
}

type someInterface interface {
    Foo() string
}

type someStringType string

type anotherStringType someStringType

func (someStringType) Foo() string {
    return "what"
}

func (anotherStringType) Foo() string {
    return "what"
}

func _() {
    var x someInterface
    x = someStringType("something")
    if z, ok := x.(someStringType); ok {
        if len(string(z)) > 0 {}
    }

    x = <error descr="Type anotherStringType is not an expression">anotherStringType</error>
    if z, ok := x.(someStringType); ok {
        if len(string(z)) > 0 {}
    }
}

type interf1 interface{}
type interf2 interf1
type interf3 interf2

func _() {
    var x string
    if _, ok := <error descr="Invalid type assertion: x.(string), (non-interface type string on left)">x</error>.(string); ok {
    }
    var y interf3
    if _, ok := y.(string); ok {
    }
}
func _() {
    var x map[string]string
    x = map[string]string{<error descr="Missing key in map literal">"a"</error>, <error descr="Missing key in map literal">"b"</error>}
    if len(x) > 2 {}
}

type someInterface interface{}

func _() {
    var x Boom
    var y someInterface
    y = x
    if _, ok := y.(Boom); ok {

    }
    switch z := y.(type) {
    case Boom:
        z.Run(aaa{})
    case *Boom:
        z.Run(aaa{})
    }
}

type baseType interface {
	Method() string
}

type middleType struct {
	baseType
}

func (c *middleType) /*def*/Method() string {
	return "Hi!"
}

type leafType struct {
	middleType
}

func _() {
	var leaf = leafType{}
	println(leaf./*ref*/Method())

	var middle = middleType{}
	println(middle./*ref*/Method())
}

// The only acceptable declarations of init at the top level: as a function with no params
// or as a method.
var <error descr="Cannot declare init, must be a function"><warning descr="Unused variable 'init'">init</warning> = 4</error>

type (
    someNotInitType string
    <error descr="Cannot declare init, must be a function">init int</error>
)

const (
    <warning descr="Unused constant 'someConst'">someConst</warning> = 1
    <error descr="Cannot declare init, must be a function"><warning descr="Unused constant 'init'">init</warning> = 4</error>
)

type someInitType struct{}

func (someInitType) init() {}

func <error descr="Duplicate function name">init</error><error descr="init function must have no arguments and no return values">(int)</error> {}

func init() <error descr="init function must have no arguments and no return values">int</error> {
    return 4
}
type GroupSlice []Group

type Group int

func (gs *GroupSlice) Merge(groups ...*Group) error {
    for _, g := range groups {
        if err := (*gs).merge(g); err != nil {
            return err
        }
    }
    return nil
}

func (gs *GroupSlice) merge(*Group) error {
    return nil
}

type TestType struct{}

func (t TestType) func1() {
}

func test90() (
TestType,
error,
) {
	return TestType{}, nil
}

func _() {
	t, _ := test90()
	t.func1()
}

func _(key1, key2 int) (string, error) {
	type MyStruct struct {
		Name  string
		Label string
	}

	var cache map[int]((map[int]MyStruct)) = make((map[int]((map[int]MyStruct))))
	tmp1, _ := cache[key1]
	tmp2, _ := tmp1[key2]
	return tmp2.Name, nil
}

// do not resolve inside type
func _() {
        var _ func(variableInFunctionType float64)
	println(<error descr="Unresolved reference 'variableInFunctionType'">variableInFunctionType</error>)
}

func _() {
	_ = 'a';<error descr="Missing '">'a</error><error descr="Unresolved reference 'aa'"><error descr="',', ';', <-, <NL>, <assign op> or '}' expected, got 'aa'">a</error>a</error><error descr="Missing '"><error descr="',', ';', <-, <NL>, <assign op> or '}' expected, got ''''">'</error>'</error>
	_ = <error descr="Empty character literal or unescaped ' in character literal">'''</error>
	_ = <error descr="Missing '">'
</error>	;_ = <error descr="Missing '">' </error><error descr="',', ';', <NL> or '}' expected, got 'a'"> </error><error descr="Unresolved reference 'a'">a</error>
	_ = 'a'
	_ = <error descr="Missing '">'a</error><error descr="',', ';', <NL> or '}' expected, got ''
'"> </error><error descr="Missing '">'
</error>}

func _() {
	_ = "a"<error descr="',', ';', <NL> or '}' expected, got '\"aaa'"> </error><error descr="New line in string">"aaa</error>
	_ = ""
	_ = <error descr="New line in string">"</error>
	_ = <error descr="New line in string">"  a</error>
}

type foo23213 struct {
    bar string
}

var (
    _ = [](*foo23213){
        {
            bar: "whatever",
        }, {

        },
    }
)

func (c <error>bufio</error>.Reader) aa(d <error>bufio</error>.MaxScanTokenSize){}