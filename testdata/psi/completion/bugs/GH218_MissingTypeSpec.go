package main

type MyStruct {
 Name string
}

type MyObj struct{
  Field1 My<caret> // place cursor here and press CTRL+Space (should not fail)
}
/**---
MyObj
MyStruct