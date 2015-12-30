package main

type Struct struct {
	String string
}

func GetChan() chan Struct {
	c := make(chan Struct, 1)
	c <- Struct{"foo"}
	close(c)
	return c
}

func main() {
	c := GetChan()
	for s := range c {
		println(s.String)
	}

}