
// +build race

package buildTags

func raceDisable() {
	a:=1
	_=a
}

func raceEnable() {
	a:=1
	_=a
}