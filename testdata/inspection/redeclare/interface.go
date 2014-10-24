package redeclare

type a interface{
	String()
	/*begin*/String/*end.Redeclare in this block*/()
}

type /*begin*/a/*end.Redeclare in this block*/ interface{
}

type b interface{
	String()
}