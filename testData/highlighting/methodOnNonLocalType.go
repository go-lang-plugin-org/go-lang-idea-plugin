package demo2031

import . "fmt"

func (_ *string) <error descr="Method defined on non-local type">Demo</error>() {

}

func (_ int) <error descr="Method defined on non-local type">Demo</error>() {

}

func (_ ScanState) <error descr="Method defined on non-local type">Demo</error>() {

}
