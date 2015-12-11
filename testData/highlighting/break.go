package main

import "fmt"

func _() {
	for i := 0; i < 10; i++ {
		fmt.Printf("%d\n", i)
		f := func() {
			<error descr="Break statement not inside a for loop, select or switch">break</error>
		}
        f()
		break
	}

	<error descr="Break statement not inside a for loop, select or switch">break</error>

	if 1 > 0 {
		<error descr="Break statement not inside a for loop, select or switch">break</error>
	}

	for i := 0; i < 10; i++ {
		defer func() {
			<error descr="Break statement not inside a for loop, select or switch">break</error>
		}()

		go func() {
			<error descr="Break statement not inside a for loop, select or switch">break</error>
		}()

		break
	}
	switch _ {
	default: _()
	case 0, 1, 2, 3: _(); break;
	case 4, 5, 6, 7: _()
	}
	
	select {
	case _ <- 0:  break;
	case _ <- 1:
	}
}