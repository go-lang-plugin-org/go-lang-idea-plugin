package main
import "fmt"
import "os"
import . "database/sql"

import (
	"aaa"
	"bbb"
)


const (
	defaultBufSize = 4096
)

var (
	ErrInvalidUnreadByte = errors.New("bufio: invalid use of UnreadByte")
	ErrInvalidUnreadRune = errors.New("bufio: invalid use of UnreadRune")
	ErrBufferFull = errors.New("bufio: buffer full")
	ErrNegativeCount = errors.New("bufio: negative count")
)

func main(aaa string, rd io.Reader, error) (ok, ok) {

	b, ok := rd.(*Reader)

	fmt.Sprintln("Hello", "oghet")


	sql.DB()
	const aaa = iota
	var bbb = iota


	switch name {

	}

	go foo()

	for 1; 1; 1 {

	}

	if ascii85.Decode() {

	}

	return 1, 1
}

var b = map[string]int{}


func f(n int) {
	for i := 0; i < 10; i++ {
		fmt.Println(n, ":", i)
	}
}

func main() {
	go f(0)
	var input string
	fmt.Scanln(&input)
}


func main23() {
	i := 1

	switch i {
	case 1:
		fmt.Print("1")
		fmt.Print("1")
		fmt.Print("1")
		fmt.Print("1")
	case 2:
	default:
		fmt.Println(1)


	}
}

func main() {
	select {
	case a <- 1:
		return a
	case a, ok := <-c3:
		break
	//
	default:
		return b
	}
}


func main() {
	tick := time.Tick(100 * time.Millisecond)
	boom := time.After(500 * time.Millisecond)


	example23123(
		"test",
		1,
	)

	for {
		select {
		case <-tick:
			fmt.Println("tick.")
		case <-boom:
			fmt.Println("Boom!")
			return
		default:
			fmt.Println("    .")
			time.Sleep(50 * time.Millisecond)
		}
	}


	response := make(chan *http.Response, 1)
	errors := make(chan *error)

	go func() {
		resp, err := http.Get("http://matt.aimonetti.net/")
		if err != nil {
			errors <- &err
		}
		response <- resp
	}()

	for {
		select {
		case r := <-response:
			fmt.Printf("%s", r.Body)
			return
		case err := <-errors:
			log.Fatal(err)
		case <-time.After(2000 * time.Millisecond):
			fmt.Println("Timed out!")
			return

		}
	}
}


type T struct {
	name        []string // name of the object
	value, a, b int      // its value
}

type x struct {
	x, y int         // a
	u    float32     // b
	_    float32     // c
	A1   *[]int      // ca
	FFF  func()      // adsfasd
	X    interface{} /* adsf*/
	X1   interface{} /* adsf*/
}

// comment

// comment

import "A"

// comment
type x a

// comment
var a int = 1

// comment
const a = iota

// comment
func a() {}

// comment
func (p *int) Length() {}

type (
	Foo1 struct {
		a int
	}

	Foo2 struct {
		b int
	}

	F func(interface{}) interface{}
)

func Test(p interface{}) error {
	switch p := p.(type) { // should be error: "p declared and not used"
	case error:
		return nil
	}
	return nil
}