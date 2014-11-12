package unresolvedSymbols

func good1(){
	var err error
	err = e1{}
	_=err.Error()
}

type e1 struct{}

func (e e1)Error()string{
	return ""
}