package functionCall

func good1(v []interface{}){
	_ = len(v)
}

func good2(v []int){
	_ = len(v)
}

func good3(v string){
	_ = len(v)
}

func bad1(v int){
	_ = len(/*begin*/v/*end.cannot use v (type int) as type Type in argument to len|CastTypeFix*/)
}