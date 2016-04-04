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
	switch <error descr="Cannot use '_' as value">_</error> {
	default: <error descr="Cannot use '_' as value">_</error>()
	case 0, 1, 2, 3: <error descr="Cannot use '_' as value">_</error>(); break;
	case 4, 5, 6, 7: <error descr="Cannot use '_' as value">_</error>()
	}
	
	select {
	case <error descr="Cannot use '_' as value">_</error> <- 0:  break;
	case <error descr="Cannot use '_' as value">_</error> <- 1:
	}
}