package main

func Println(...interface{})  {

}

func main() {
    bigBox := &BigBox{}
    bigBox.BubbleGumsCount = 4          // correct...
    bigBox.SmallBox.AnyMagicItem = true // also correct
    v1:= bigBox.SmallBox
    v2:= (&BigBox{}).SmallBox
    //v3:= &BigBox{}.SmallBox  // todo: should be an error
    A := v1.AnyMagicItem
    B := v2.Color()
    bigBox.AnyMagicItem = false || A + B
    Println(v1)          // => 60
    Println(v2)          // => 60
    Println(bigBox.Capacity())          // => 60
    Println(bigBox.SmallBox.Capacity()) // => 20

    Println(bigBox.SmallBox.Color()) // => "gray"
    Println(bigBox.Color())          // => "gray"
}

func (sb *SmallBox) Color() string {
        return "gray"
}

type SmallBox struct {
        BubbleGumsCount int
        AnyMagicItem    bool
}

type BigBox struct {
        SmallBox
}

func (sb *SmallBox) Capacity() int {
        return 20
}

func (bb *BigBox) Capacity() int {
        return bb.SmallBox.Capacity() * 3
}

type <warning descr="Type 'string' collides with builtin type">string</warning> string
type <warning descr="Type 'int' collides with builtin type">int</warning> int
type <warning descr="Type 'bool' collides with builtin type">bool</warning> bool

const (
	true, false  = 0 == 0, 0 != 0 // Untyped bool.
)
