package main

type Map map[string]

func (*Map) Method() {

}
func main() {
	var myMap = make(Map)
	myMap.<caret>
}

/**---
Method
