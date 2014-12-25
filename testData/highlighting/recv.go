package main

func main() {
    var ch = make(chan int)
    select {
    case <error>test</error> := <-ch: {
        test := 1
        print(test)
    }
    }
}