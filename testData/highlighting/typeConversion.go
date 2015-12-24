package main

type (
       int64Val int64
       boolVal  bool
       Value    interface {
               Demo()
       }
)

func (int64Val) Demo() {}
func (boolVal) Demo() {}

func _(x, y Value) {
       switch x := x.(type) {
       case boolVal:
               y := y.(boolVal)
               _, _ = x, y
       case int64Val:
               b := int64(y.(int64Val))
               _ = b
       }

}

func main() {}