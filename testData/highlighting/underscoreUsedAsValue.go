package main

var demov = <error descr="Cannot use '_' as value">_</error>
var (
	demov1, demov2 = <error descr="Cannot use '_' as value">_</error>, <error descr="Cannot use '_' as value">_</error>
)
const democ = <error descr="Cannot use '_' as value">_</error>
const (
	democ1, democ2 = <error descr="Cannot use '_' as value">_</error>, <error descr="Cannot use '_' as value">_</error>
)

func main() {
	println(<error descr="Cannot use '_' as value">_</error>, "hello1")
	a, b:= 1, <error descr="Cannot use '_' as value">_</error>
	_, _, _, _ = a, b, demov, democ
	_, _, _, _ = demov1, demov2, democ1, democ2
	c := 1 + <error descr="Cannot use '_' as value">_</error> + (<error descr="Cannot use '_' as value">_</error>)
	println(<error descr="Cannot use '_' as value">_</error> + (<error descr="Cannot use '_' as value">_</error>))
	println(c)

	select {
	case <error descr="Cannot use '_' as value">_</error> <- 0: break;
	case <error descr="Cannot use '_' as value">_</error> <- 1: break;
	case _ = <- 1: break;
	}

	for _, _ = range <error descr="Cannot use '_' as value">_</error>  {
	}
}
