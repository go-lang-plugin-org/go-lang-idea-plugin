package redeclare

type a interface{
	String()
	/*begin*/String/*end.Redeclare in this block*/()
}

type b interface{
	String()
}