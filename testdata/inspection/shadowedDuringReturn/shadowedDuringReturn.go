package main

import (
	"fmt"
)

func main() {
}

func bad1()(err1 error){
	if true{
		err1:=fmt.Errorf("123")
		if err1!=nil{
			/*begin*/return/*end.Varibles err1 shadowed during return*/
		}
	}
	return
}

func bad2()(err2 error){
	if true{
		err2:="123"
		if true {
			if err2 != "" {
				/*begin*/return/*end.Varibles err2 shadowed during return*/
			}
		}
	}
	return
}

func bad3()(err3 error){
	if true{
		if true {
			if err3 != nil {
				return
			}
		}
		err3:="123"
		if err3 != "" {
			/*begin*/return/*end.Varibles err3 shadowed during return*/
		}
	}
	return
}

func bad4()(err4,v4 int){
	if true{
		if true {
			if err4 != nil {
				return
			}
		}
		err4:="123"
		v4:="abc"
		if err4 != "" && v4!="" {
			/*begin*/return/*end.Varibles err4, v4 shadowed during return*/
		}
	}
	return
}

func good1()(err error){
	if true{
		err:=fmt.Errorf("123")
	}
	return
}


func good2()(err error){
	if true{
		if true{
			return
		}
		fmt.Println(err)
		err:="123"
		fmt.Println(err)
	}
	return
}
