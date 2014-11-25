package main

import "<error></error>"
import fmt "<error></error>"
import "net/http"
import "io"

func  main() {
	test := <error>test</error>
	Println(test)
	fmt.<EOLError></EOLError>
}

type aaa aaa

type Boom struct {
   err aaa
}

func (b *Boom) Run(a aaa) (r1 aaa, r2 aaa) {
   b.err + a + r1 + r2
<error>}</error>

func <warning>foo</warning>() {
    i := 1
    for (i) {return 0}
    if (i) {return <error>j</error>}

    headers := 1
    for _, h := range headers {
      h++
    }
}

var nil int

type int int
type byte byte
type bool bool
type float32 float32
type string string

type T struct {
	a int
}
func (tv  T) Mv(a int) int         { return 0 }  // value receiver
func (tp *T) Mp(f float32) float32 { return 1 }  // pointer receiver

var t T

func <warning>bar</warning>() {
    t.Mv(7)
    T.Mv(t, 7)
    (T).Mv(t, 7)
    f1 := T.Mv; f1(t, 7)
    f2 := (T).Mv; f2(t, 7)
}


func <error>foo</error>() {
    a := &A{}
    b := &B{b:"bbb"}
    e := &Empty{}
    y := make(A, 10)
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
func <warning>BenchmarkName</warning>(b *AA) {
     b.N
}

func make(o interface{}, args ...interface{}) {
}

func new(o interface{}) {
  func(i interface{}) {
    Println(o)
    Println(i)
  }
}

func <warning>concurrently</warning>(integers []int) []int {
  ch := make(chan int)
  <error>responses</error> := []int{}
  for _, i := range integers {
      go func(j int) {
          ch <- j * j
      }(<error>j</error>)
  }
  for _, i := range integers {
      go func(j int) {
          ch <- j * j
      }(i)
  }
  err := 1
  _, err = 1, 1
  return integers
}

func Println(o ...interface{})  {
}

func <warning>innerTypes</warning>() {
	type connError struct {
		cn  int
	}
	ch := make(chan connError)
	Println(ch.cn)
}

type Iface interface {
  Boo() int
}

const name1 int = 10

func <warning>goo</warning>(st interface {Foo()}, st1 Iface) {
    <error>name1</error>, <error>name1</error> = 1, 2
    Println(st.Foo() + st1.Boo())
    if _ := 1 {
      return
    }
}

func <warning>labelsCheck</warning>() { goto Label1; Label1: 1; goto <error>Label2</error>}

type compositeA struct { int }
type compositeB struct { byte }

func <warning>composite</warning> () {
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
func composite2() (a *compositeA, b *compositeB) {
	return new(compositeA), new(compositeB)
}

func <warning>do</warning>(o interface {test1() int}) {
	Println(o.test1())
}

func <warning>dial</warning>() (int) {
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

func <warning>main2</warning>() {
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

type Response struct { ResponseWriter }
type ResponseWriter interface { Header() Header }
type Header int

func (h Header) Add(key, value string) { }

func (r Response) AddHeader(header string, value string) Response {
	rr := r.Header()
	rr.Add()
	r.Header().Add(header, value)
	return r
}

type WebService struct { rootPath string }
func (w *WebService) Path(root string) *WebService { return w }
func (w *WebService) GET(subPath string) *RouteBuilder { return new(RouteBuilder) }
type RouteBuilder struct { bool }
func (w *WebService) Route(builder *RouteBuilder) *WebService { return w }
func <warning>WebServiceTest</warning>() {
	ws1 := new(WebService).Path("/")
	ws1.GET().bool
	ws1.Route(ws1.GET("/{type}/{id}"))
}

type ServiceError struct {
	Code    int
	Message string
}


func <warning>typeAssert</warning>() {
  err := nil
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

func <warning>typeClashes</warning>(r *Request)  {
    r.Request.URL
}

type ReadCloser interface {
    io.Reader
    io.Closer
}

func <warning>processReadCloser</warning>(b ReadCloser)  {
    b.<error>Closer</error>()
    b.Close()
}

func <warning>TestTemplateToRegularExpression</warning>() {
var tempregexs = []struct {
    template, regex         string
    literalCount, varCount int
}{
    {"", "^(/.*)?$", 0, 0},
    {"/a/{b}/c/", "^/a/([^/]+?)/c(/.*)?$", 2, 1},
    {"/{a}/{b}/{c-d-e}/", "^/([^/]+?)/([^/]+?)/([^/]+?)(/.*)?$", 0, 3},
    {"/{p}/abcde", "^/([^/]+?)/abcde(/.*)?$", 5, 1},
}
    for i, fixture := range tempregexs {
          fixture.regex
    }
}

type Route struct {
}

func (r Route) matchesContentType() {
}

func <warning>eachProcessing</warning>() {
    rs := []Route{}
    for _, each := range rs {
        if each.matchesContentType() {
        }
    }
    var rs2, <error>i</error> = makeTuple()
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

func <warning>moreRanges</warning>() {
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

func <warning>main123</warning>() {
    response := make(chan *R, 1)
    r := <- response
    Println(r.Body)
    Println(response.<error>Body</error>)
}

func <warning>duplicates</warning>(a int, <error>a</error> int, c, d, <error>c</error> int) (<error>a</error>, <error>d</error>, x int) {
  return 1,1,1
}

func <warning>variadic</warning>(a int,  d<error>...</error> int, c string) (y,x,z int) {
    return 1,1,1
} 

func <warning>variadic2</warning>(a int, c, d<error>...</error> int) (y,x,z <error>...</error>int) {
    return 1,1,1
}

type RGBA struct {
    R, G, B, A uint8
}

type (
    Color RGBA
)

func <warning>name</warning>(col Color) string {
    Println(col.B)  
    
    var testdata *struct {
        a *[7]int
    }
    Println(testdata.a)
<error>}</error>

var <error>Name11</error> string  = ""
var <error>nmame11</error> string  = ""
