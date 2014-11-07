package main

func bad1(){
	b:=1
	/*begin*/_,b/*end.No new variables on left side of :=*/:=1,2
}