package main

func _() {
	invalid:
	outer:
	for {
		inner:
		for {
			break <error descr="Unresolved label 'invalid'">invalid</error>
			break inner
			break outer
		}

		break <error descr="Unresolved label 'invalid'">invalid</error>;
		break <error descr="Unresolved label 'inner'">inner</error>;
		break outer;
		func () {
			funcLit:
			for {
				break <error descr="Unresolved label 'invalid'">invalid</error>;
				break <error descr="Unresolved label 'inner'">inner</error>;
				break <error descr="Unresolved label 'outer'">outer</error>;
				break funcLit;
			}
		}
	}
}