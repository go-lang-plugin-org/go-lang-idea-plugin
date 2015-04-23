package main; 
import "to_import/<error descr="Cannot resolve file 'testdata'">testdata</error>"; 

type Outer struct { *testdata.<error descr="Unresolved type 'Reader'">Reader</error> };
func main() {}