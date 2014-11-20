package main

func good1(v []interface{}) {
	_ = len(v)
}

func good2(v []int) {
	_ = len(v)
}

func good3(v string) {
	_ = len(v)
}

func bad1(v int) {
	_ = len(/*begin*/v/*end.invalid argument v (type int) for len*/)
}

func bad2(v interface{}) {
	_ = len(/*begin*/v/*end.invalid argument v (type interface{}) for len*/)
}

func main() {
	/*begin*/len()/*end.missing argument to len*/
	len(/*begin*/1/*end.invalid argument 1 (type int) for len*/, /*begin*/2.0/*end.extra argument to len*/)

	len((chan int)(nil))
	len((<-chan int)(nil))
	len((chan<- int)(nil))
}
