package main

type (
ds1 struct {
}

ds2 struct {

}
)

func (d ds1) d1(param1 string, param2 int) ds2 {
    return ds2{}
}

func (d ds2) d2(param1 string, param2 int) int {
    return 1
}

func main() {
    d := ds1{}
    d.d1("1", 2).d2(<caret>)
}