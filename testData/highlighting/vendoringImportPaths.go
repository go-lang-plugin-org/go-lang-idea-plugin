package a

import "vendoringPackage"
import "<error descr="Cannot resolve file 'subVendoringPackage'">subVendoringPackage</error>"

func _() {
  vendoringPackage.Hello();
  subVendoringPackage.<error descr="Unresolved reference 'Hello'">Hello</error>();
}