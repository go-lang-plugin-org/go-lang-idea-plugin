package main

func main() {
    Ok1() + 
    Ok2(42) +
    Ok3() +
    Ok4() +
    Ok5() +
    Ok6() +
    Ok7() +
    Ok8() +
    NoReturn1() +
    NoReturn2() +
    NoReturn3(1) +
    NoReturn4(1) +
    NoReturn5(1) +
    NoReturn6(1) +
    NoReturn65(1) +
    NoReturn7(1) +
    NoReturn8(1) +
    NoReturn9(1) 
    
    rsaPKCS1v15SignatureAlgorithmForHashID(42)
    rsaPKCS1v15SignatureAlgorithmForHashID2(42)

    <error descr="Function literal evaluated but not used">func</error>() int {
    <error descr="Missing return at end of function">}</error>
}

func Ok1() int {
    return 3
    /*  */
    //
}

func Ok2(a int) int {
    if a > 0 {
        return 3
    }
    panic("NO!")
}

func Ok3() int {
    Label:
    return 3
}

func Ok4() bool {
    for {

    }
}

func Ok5() bool {
    a := 0
    if a > 0 {
        return true
    }else if a < 0 {
        return true
    }else {
        return false
    }
}

func Ok6() bool {
    a := make(chan bool)
    select {
    case <-a:
        return false
    default:
        return true
    }
}

func Ok7() bool {
    a := 3
    switch a {
        case 1:
        return false
        case 2:
        return true
        default:
        return true
    }
}

func Ok8() bool {
    for a := 0;; a++ {

    }
}

func NoReturn1() int {
<error descr="Missing return at end of function">}</error>

func NoReturn2() (int, int) {
<error descr="Missing return at end of function">}</error>


func NoReturn3(a int) (int, int) {
    if a == 5 {
        return a, a
    }
<error descr="Missing return at end of function">}</error>

func NoReturn4(aa int) (int, int) {
    a := 3
    for a > 0 {

    }
<error descr="Missing return at end of function">}</error>

func NoReturn5(aa int) (int, int) {
    a := 3
    if a > 0 {
        return 0, 0
    }
<error descr="Missing return at end of function">}</error>

func NoReturn6(aa int) (int, int) {
    a := 3
    if a > 0 {
        return 0, 0
    }else if a < 2 {
        return 0, 0
    }
<error descr="Missing return at end of function">}</error>

func NoReturn65(aa int) (int, int) {
    a := 3
    if a > 0 {
        return 0, 0
    }else {

    }
<error descr="Missing return at end of function">}</error>

func NoReturn7(aa int) (int, int) {
    a := 3
    switch a {
        case 1:
        return 0, 0
    }
<error descr="Missing return at end of function">}</error>

func NoReturn8(aa int) (int, int) {
    a := 3
    switch a {
        case 1:
        return 0, 0
        default:
    }
<error descr="Missing return at end of function">}</error>

func NoReturn9(aa int) (int, int) {
    a := make(chan bool)
    select {
    case <-a:
        return 0, 0
    default:
    }
<error descr="Missing return at end of function">}</error>

func rsaPKCS1v15SignatureAlgorithmForHashID(hashID int) string {
	switch {
	case hashID == 512: return "asd2"
	case hashID == 512: return "asd1"
	case hashID == 512: fallthrough
	default: return "asdas"
	}
}

func rsaPKCS1v15SignatureAlgorithmForHashID2(hashID int) string {
	switch {
	case hashID == 512: return "asd2"
	case hashID == 512: return "asd1"
	case hashID == 512:
	default: return "asdas"
	}
<error descr="Missing return at end of function">}</error>
