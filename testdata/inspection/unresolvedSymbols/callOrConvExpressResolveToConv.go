package unresolvedSymbols

type T1 int

func (t T1)String()string{
	return "1"
}
func f1(){
	d0 := T1(1)
	_ := d0.String()
	d0./*begin*/NotExist/*end.Unresolved symbol: 'NotExist'*/()
}
