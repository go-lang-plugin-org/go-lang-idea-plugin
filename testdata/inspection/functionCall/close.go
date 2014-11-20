package main

func good1() {
	close((chan<- int)(nil))
	close((chan int)(nil))
}

func bad1(v int){
	/*begin*/close()/*end.missing argument to close*/
	close(/*begin*/1/*end.invalid operation: 1 (non-chan type int)*/, /*begin*/""/*end.extra argument to close*/)
	close(/*begin*/""/*end.invalid operation: "" (non-chan type string)*/)
	close(/*begin*/(<-chan int)(nil)/*end.invalid operation: (<-chan int)(nil) (cannot close receive-only channel)*/)
	close(/*begin*/v/*end.invalid operation: v (non-chan type int)*/)
}

func main() {
}
