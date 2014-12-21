package main

type Common struct {
}

type Derived struct {
	Common
	derivedMember int
}

func (c*Common) /*def*/commonMethod() {

}

func (c*Derived) derivedMethod() {

}

func f() {
	var x Derived

	x./*ref*/commonMethod();
}
