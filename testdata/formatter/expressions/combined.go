package main

var (
  _ = b  &      0x0f     <<      4  |  b     >>   4
  _ =  a * b - - a
 	_ = ( a * b ) - - a
	_ =(a - ( a *b )-a)<<3
	_ =   a     [     2*4 - 4]
)