package main

type t1 struct {
	t2
	FCode string
	FFunc1 func()error()
}

type t2 struct {
	FStatus int
}

func (t t1) Method1() error {
	return nil
}

func a() {
	c := t1{}
	//good
	c.FStatus = 1
	c.FCode = "1"
	c.FFunc1 = func() error {
		return nil
	}
	c.FFunc1 = c.Method1
	d := t1{
		FFunc1: c.Method1,
	}
	_ = d
	//bad
	c./*begin*/notFound1/*end.Unresolved symbol: 'notFound1'*/ = 1
	c.FStatus = c./*begin*/notFound2/*end.Unresolved symbol: 'notFound2'*/
	d := t1{
		FFunc1: c./*begin*/notFound3/*end.Unresolved symbol: 'notFound3'*/,
	}
}