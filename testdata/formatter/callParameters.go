package main

func main(){
buf = make([]byte, 1 + len(msg.name))
}
-----
package main

func main() {
	buf = make([]byte, 1+len(msg.name))
}
