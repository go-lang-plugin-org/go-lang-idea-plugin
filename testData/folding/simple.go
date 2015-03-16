package foo

import <fold text='...'>(
  "aaaa"
  "fmt"
)</fold>

func test() <fold text='{...}'>{
  return 1
}</fold>

func (i int) test() <fold text='{...}'>{
  return 1
}</fold>

type A struct <fold text='{...}'>{
    in int
}</fold>

type C interface <fold text='{...}'>{
    Foo() int
}</fold>
