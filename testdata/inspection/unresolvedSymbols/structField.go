package main

type t1 struct {
	t2
	Code string
}

type t2 struct {
	Status int
}

func a() {
	c := t1{}
	c.Status = 1
	c.Code = "1"
	c./*begin*/notFound/*end.Unresolved symbol: 'notFound'*/ = 1
}