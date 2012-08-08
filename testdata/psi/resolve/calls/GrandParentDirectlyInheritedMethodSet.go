package main

type MoreCommon struct {
}

type Common struct {
	MoreCommon
}

type Derived struct {
	Common
	derivedMember int
}

func (c*MoreCommon) /*def*/moreCommonMethod() {

}

func (c*Common) commonMethod() {

}

func (c*Derived) derivedMethod() {

}

func f() {
	var x Derived

	x./*ref*/moreCommonMethod();
}
