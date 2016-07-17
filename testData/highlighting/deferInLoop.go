package deferinloop

func noop() {}

type b int

func (a b) meth() {
	for {
		<weak_warning descr="Possible resource leak, 'defer' is called in a for loop.">defer</weak_warning> noop()
	}
}

func _() {
	for {
		func (){
			defer noop()
		}()
	}

	func (){
		defer noop()
	}()

	for {
		switch 1 {
		case 2: <weak_warning descr="Possible resource leak, 'defer' is called in a for loop.">defer</weak_warning> noop();
		}
	}

	defer noop();
	for {
		<weak_warning descr="Possible resource leak, 'defer' is called in a for loop.">defer</weak_warning> noop()
	}
}

