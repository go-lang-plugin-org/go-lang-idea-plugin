package main
import "reflect"
type T interface {
Method1() T
Method2()
/*def*/Method3()
}

type IoC interface {
//Gets an instance of the given class
Get(class reflect.Type,args ...interface {}) interface {}
//Puts into the container an instance, can fail if the container not accepts the instance
Put(injections ...interface {}) []error
//when param is a function => Inject values matching the parameters, return an closure ex: IoC.Inject(func(validator*validator.V,postdata*webapp.Post){}).(func())()
Inject(fn interface {},injections ...interface {}) func(args ...interface {}) interface {}
//is the same as call IoC.Inject(func(injections...))()
Call(fn interface {},injections ...interface {}) interface {}
//returns true if there is some implementation of the module in the current scope will not resolve top containers
HasModule(reflect.Type) bool
}

-----
package main

import "reflect"

type T interface {
	Method1() T
	Method2()
	/*def*/ Method3()
}

type IoC interface {
	//Gets an instance of the given class
	Get(class reflect.Type, args ...interface{}) interface{}
	//Puts into the container an instance, can fail if the container not accepts the instance
	Put(injections ...interface{}) []error
	//when param is a function => Inject values matching the parameters, return an closure ex: IoC.Inject(func(validator*validator.V,postdata*webapp.Post){}).(func())()
	Inject(fn interface{}, injections ...interface{}) func(args ...interface{}) interface{}
	//is the same as call IoC.Inject(func(injections...))()
	Call(fn interface{}, injections ...interface{}) interface{}
	//returns true if there is some implementation of the module in the current scope will not resolve top containers
	HasModule(reflect.Type) bool
}
