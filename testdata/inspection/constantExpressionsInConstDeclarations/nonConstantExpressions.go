package main

const a = /*begin*/a1 + 3.0/*end.Constant expression expected*/
const b = /*begin*/int(a1) / 4/*end.Constant expression expected*/
const c = /*begin*/f(a)/*end.Constant expression expected*/
const d = /*begin*/3.14 / 0.0/*end.Constant expression expected*/

func f(int) {
}

