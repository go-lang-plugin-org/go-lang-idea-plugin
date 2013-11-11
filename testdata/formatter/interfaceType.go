package main

type T interface {
Method1() T
Method2()
/*def*/Method3();
}
-----
package main

type T interface {
	Method1() T
	Method2()
	/*def*/ Method3()
}
