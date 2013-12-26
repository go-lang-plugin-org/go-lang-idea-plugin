package main
func dontcompile() {
switch true {
case 5 == 5:

}
}

-----
package main

func dontcompile() {
	switch true {
	case 5 == 5:

	}
}
