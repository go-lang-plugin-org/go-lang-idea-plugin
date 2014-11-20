package main

import (
	"io"
)

func Ok1() (n int) {
	return
}

func Ok2() (n, _ int) {
	return
}

func Ok3() (a, b int) {
	return
}

func Ok4() (int, int) {
	go func() int {
		for i := 0; i < 10; i++ {
			return 5
		}
		return 3
	}()
	return Ok3()
}

func Ok5(w io.Writer) (n int, err error) {
	return w.Write(nil)
}

func NotEnough1() int {
	/*begin*/return/*end.not enough arguments to return|ChangeReturnsParametersFix*/
}

func NotEnough2(a int) (b, c int) {
	if a == 1 {
		return
	} else if a == 2 {
		/*begin*/return 1/*end.not enough arguments to return|ChangeReturnsParametersFix*/
	} else if a == 3 {
		return 1, 2
	}
	return 3, 4, /*begin*/5/*end.extra argument to return|ChangeReturnsParametersFix*/
}

func NotEnough3(a int) (int, int) {
	if a == 1 {
		/*begin*/return/*end.not enough arguments to return|ChangeReturnsParametersFix*/
	} else if a == 2 {
		/*begin*/return 1/*end.not enough arguments to return|ChangeReturnsParametersFix*/
	} else if a == 3 {
		return 1, 2
	}
	return 3, 4, /*begin*/5/*end.extra argument to return|ChangeReturnsParametersFix*/
}

func TooMany1(a, b int) {
	if a > 0 {
		return /*begin*/a + b/*end.extra argument to return|ChangeReturnsParametersFix*/
	}
	return /*begin*/a/*end.extra argument to return|ChangeReturnsParametersFix*/
}

func NotMatchType() (string, int64) {
	return "ok", /*begin*/"not ok"/*end.cannot use "not ok" (type string) as type int64 in return argument|CastTypeFix|ChangeReturnsParametersFix*/
}

func NotMatchTypeCall() (string, int64) {
	return /*begin*/Ok2()/*end.cannot use Ok2() (type int) as type string in return argument|ChangeReturnsParametersFix*/
}

func IsError() error {
	return nil
}

func FnRet() func(struct {Name string}) {
	return func(arg struct {Name string}) {

	}
}

type StructT struct {

}

func (t *StructT) Method1(arg,arg1 string) bool {
	return arg == arg1
}

func (t *StructT) MethodGet() func(string,string)bool {
	return t.Method1
}

func testChan() {
	ch := make(chan int)
	go func() {
		if _, ok := <- ch; ok {

		}
	}()
	ch <- 1
}

//Issue #630
type MyError struct {}

func (m MyError) Error() string {
	return "foo"
}

func foo() error {
	return MyError{}
}
