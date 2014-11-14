package constantExpressionsInConstDeclarations

type MyString string
type Duration int64

const good1 = uint(iota)               // iota value of type uint
const good2 = float32(2.718281828)     // 2.718281828 of type float32
const good3 = complex128(1)            // 1.0 + 0.0i of type complex128
const good4 = float32(0.49999999)      // 0.5 of type float32
const good5 = string('x')              // "x" of type string
const good6 = string(0x266c)           // "♬" of type string
const good7 = Duration(int64(1<<63 - 1))
const good8 = MyString("foo" + "bar")  // "foobar" of type MyString

const bad1 = /*begin*/string([]byte{'a'})/*end.Constant expression expected*/      // not a constant: []byte{'a'} is not a constant
const bad2 = /*begin*/(*int)(nil)/*end.Constant expression expected*/              // not a constant: nil is not a constant, *int is not a boolean, numeric, or string type
const bad3 = /*begin*/int(1.2)/*end.Constant expression expected*/                 // illegal: 1.2 cannot be represented as an int
const bad4 = /*begin*/string(65.0)/*end.Constant expression expected*/             // illegal: 65.0 is not an integer constant

