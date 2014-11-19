package functionCall

func good1(){
	panic(1)
	panic("hello")
	/*begin*/panic()/*end.missing arguments to panic*/
}