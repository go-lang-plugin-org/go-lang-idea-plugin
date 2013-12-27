package main

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
    }else{
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
    for a:=0;;a++ {

    }
}

func NoReturn1() int {
/*begin*/}/*end.Function ends without a return statement|AddReturnStmtFix|RemoveFunctionResultFix*/

func NoReturn2() (int, int) {
/*begin*/}/*end.Function ends without a return statement|AddReturnStmtFix|RemoveFunctionResultFix*/


func NoReturn3(a int) (int, int) {
    if a == 5 {
        return a, a
    }
/*begin*/}/*end.Function ends without a return statement|AddReturnStmtFix|RemoveFunctionResultFix*/

func NoReturn4(a int) (int, int) {
    a := 3
    for a > 0 {

    }
/*begin*/}/*end.Function ends without a return statement|AddReturnStmtFix|RemoveFunctionResultFix*/

func NoReturn5(a int) (int, int) {
    a := 3
    if a > 0 {
        return 0, 0
    }
/*begin*/}/*end.Function ends without a return statement|AddReturnStmtFix|RemoveFunctionResultFix*/

func NoReturn6(a int) (int, int) {
    a := 3
    if a > 0 {
        return 0, 0
    }else if a < 2 {
        return 0, 0
    }
/*begin*/}/*end.Function ends without a return statement|AddReturnStmtFix|RemoveFunctionResultFix*/

func NoReturn6(a int) (int, int) {
    a := 3
    if a > 0 {
        return 0, 0
    }else {

    }
/*begin*/}/*end.Function ends without a return statement|AddReturnStmtFix|RemoveFunctionResultFix*/

func NoReturn7(a int) (int, int) {
    a := 3
    switch a {
    case 1:
        return 0, 0
    }
/*begin*/}/*end.Function ends without a return statement|AddReturnStmtFix|RemoveFunctionResultFix*/

func NoReturn8(a int) (int, int) {
    a := 3
    switch a {
    case 1:
        return 0, 0
    default:
    }
/*begin*/}/*end.Function ends without a return statement|AddReturnStmtFix|RemoveFunctionResultFix*/

func NoReturn9(a int) (int, int) {
    a := make(chan bool)
    select {
    case <-a:
        return 0, 0
    default:
    }
/*begin*/}/*end.Function ends without a return statement|AddReturnStmtFix|RemoveFunctionResultFix*/
