package foo

func NewCipher(key []byte) (cipher.Block , error) {
	return c, nil
}

func simpleF() int {
    return 2
}

func complexF1() (re float64, im float64) {
    return -7.0, -4.0
}

func test() {
    switch tag {
        default: s3()
        case 0, 1, 2, 3: s1()
        case 4, 5, 6, 7: s2()
    }

    switch x := f(); {  // missing switch expression means "true"
        case x < 0: return -x
        default: return x
    }
    switch x := f(); 1==1 {  // missing switch expression means "true"
        case x < 0: return -x
        default: return x
    }

    switch {
        case x < y: f1()
        case x < z: f2()
        case x == 4: f3()
    }
}

func emptyFor() {
    for s.accept(decimalDigits) {

    }
}

func emptyIf() {
  if err := q.Send(&Cmd{U: parsed, M: method}); err != nil {
  }
}

func emptyForRange() {
      for key, value := range collection {

      }

}

func emptyClauses() {
    verb := "1"[0]
    switch verb {
        case 'p':
        default:
    }
}

func (h extraHeader) Write(w io.Writer) {
	for i, v := range []string{h.contentType, h.contentLength, h.connection, h.date, h.transferEncoding} {
		if v != "" {
			w.Write(extraHeaderKeys[i])
			w.Write(colonSpace)
			io.WriteString(w, v)
			w.Write(crlf)
		}
	}
}

func main() {
    if 1 != 1 {
    }

	select {
	case ce := <-ch:
		return ce.cn, ce.err
	case <-time.After(c.netTimeout()):
		// Too slow. Fall through.
	}
}


var days = [...]string{
	"Sunday",
	"Monday",
	"Tuesday",
	"Wednesday",
	"Thursday",
	"Friday",
	"Saturday",
}

var htmlReplacer = strings.NewReplacer(
"&", "&amp;",
"<", "&lt;",
">", "&gt;",
// "&#34;" is shorter than "&quot;".
`"`, "&#34;",
// "&#39;" is shorter than "&apos;" and apos was not in HTML until HTML5.
"'", "&#39;",
)

func tets() {
  v = v.assignTo("reflect.Value.Call", targ, (*interface{})(addr))
  type test interface {}
  t := new(test)
  v := (interface{})(t)
}

func (*ctx) Errorf(string, ...interface{}) {}

var alreadyAddedErrors = map[pb.TaskQueueServiceError_ErrorCode]bool{
  pb.TaskQueueServiceError_TASK_ALREADY_EXISTS: true,
}

var alreadyAddedErrors2 = map[pb.TaskQueueServiceError_ErrorCode]bool{
  pb.TaskQueueServiceError_TASK_ALREADY_EXISTS: true,
  pb.TaskQueueServiceError_TOMBSTONED_TASK:     true,
}

var a =    []byte(b.Type + "-----\n")

var b = map[string]int{
    "",
}

func foo() {
    func () {
    }()
}

func hello( id string,
) error {
    return nil
}

func hello( id string, ) error {
    return nil
}

func sourceReader(files <-chan *File) {
    for _, page := range []*File{} {
        if page.Contents == "" {
            break
        }
    }

}

func sourceReader1() {
    for _, page := range []*File{} {
    }
}

func sourceReader1() {
    for _, page := range *File{} { // error as expected
    }
}

func main() {
    a := []byte{}
    b := append(
    []byte("demo"),
    a..., // example fails here
)
    _ = b
}

func main() {
    for a:= 1; a<10; a++ {
        goto err
    }
    err:
}

func main() {
    if err := foo(demo{}); err!= nil {
        panic(err)
    }

    if err := (demo{}); err!= nil {
        panic(err)
    }
    for b, i := M{}, 10; i < 10; i++ { // error
        fmt.Println(v)
    }
    for b, i := (M{}), 10; i < 10; i++ {
        fmt.Println(v)
    }
}

type name struct {
    foo string `xml:""`
}

func TestFor() {
    type name1234 interface {

    }
    var v []int
    call(func() {
        for range v { // v is not resolved here
        }
    })
}

func TestIf() {
    var v string
    call(func() {
        if v == "" {
        }
        if "" == v {  // v is not resolved here
        }
    })
}

func call(f func()) {
    f()
}

type SensorFactory struct {
	Sensors map[string]string
}

func InitSensorFactory() *SensorFactory {
	factory := new (SensorFactory)
	factory.Sensors = make(map[string]string)
	return factory
}
