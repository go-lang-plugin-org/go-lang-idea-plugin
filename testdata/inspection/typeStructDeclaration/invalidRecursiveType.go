
type T1 struct {
    /*begin*/T1/*end.Invalid recursive type T1*/
}


type T2 struct {
    /*begin*/T3/*end.Invalid recursive type T2*/
}
type T3 struct {
    /*begin*/T2/*end.Invalid recursive type T3*/
}


type T4 struct {
    *T4
}