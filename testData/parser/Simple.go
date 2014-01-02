package foo

func NewCipher(key []byte) (cipher.Block , error) {
	return c, nil
}

func simpleF() int {
    return 2
}

func complexF1() (re float64, im float64) {
    return -7.0, -4.0
}

func test() {
    switch tag {
        default: s3()
        case 0, 1, 2, 3: s1()
        case 4, 5, 6, 7: s2()
    }

    switch x := f(); {  // missing switch expression means "true"
        case x < 0: return -x
        default: return x
    }
    switch x := f(); 1==1 {  // missing switch expression means "true"
        case x < 0: return -x
        default: return x
    }

    switch {
        case x < y: f1()
        case x < z: f2()
        case x == 4: f3()
    }
}