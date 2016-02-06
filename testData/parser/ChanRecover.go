package n

func f() {
	// test case from issue 13273
	<-chan int((chan int)(nil))

	<-chan int(nil)
	<-chan chan int(nil)
	<-chan chan chan int(nil)
	<-chan chan chan chan int(nil)
	<-chan chan chan chan chan int(nil)

	<-chan <-chan int(nil)
	<-chan <-chan <-chan int(nil)
	<-chan <-chan <-chan <-chan int(nil)
	<-chan <-chan <-chan <-chan <-chan int(nil)

	<-chan (<-chan int)(nil)
	<-chan (<-chan (<-chan int))(nil)
	<-chan (<-chan (<-chan (<-chan int)))(nil)
	<-chan (<-chan (<-chan (<-chan (<-chan int))))(nil)

	<-(<-chan int)(nil)
	<-(<-chan chan int)(nil)
	<-(<-chan chan chan int)(nil)
	<-(<-chan chan chan chan int)(nil)
	<-(<-chan chan chan chan chan int)(nil)

	<-(<-chan <-chan int)(nil)
	<-(<-chan <-chan <-chan int)(nil)
	<-(<-chan <-chan <-chan <-chan int)(nil)
	<-(<-chan <-chan <-chan <-chan <-chan int)(nil)

	<-(<-chan (<-chan int))(nil)
	<-(<-chan (<-chan (<-chan int)))(nil)
	<-(<-chan (<-chan (<-chan (<-chan int))))(nil)
	<-(<-chan (<-chan (<-chan (<-chan (<-chan int)))))(nil)

	type _ <-<-chan int // ERROR "unexpected <-, expecting chan"
	<-chan int // ERROR "unexpected <-, expecting chan|expecting {" (new parser: same error as for type decl)
	<-<-chan int // ERROR "unexpected <-, expecting chan|expecting {" (new parser: same error as for type decl)

	type _ <-chan <-int // ERROR "unexpected int, expecting chan|expecting chan"
	<-chan<-int // ERROR "unexpected int, expecting chan|expecting {" (new parser: same error as for type decl)
}