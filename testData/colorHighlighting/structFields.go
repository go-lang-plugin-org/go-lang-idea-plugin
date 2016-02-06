package main

type <info descr="null"><info descr="GO_PACKAGE_LOCAL_STRUCT">type1</info></info> struct {
	<info descr="null"><info descr="GO_STRUCT_LOCAL_MEMBER">var1</info></info> int
}

func <info descr="null"><info descr="GO_LOCAL_FUNCTION">main</info></info>() {
	<info descr="null"><info descr="GO_LOCAL_VARIABLE">struct1</info></info> := <info descr="null"><info descr="GO_PACKAGE_LOCAL_STRUCT">type1</info></info>{<info descr="null"><info descr="GO_STRUCT_LOCAL_MEMBER">var1</info></info>: 1}
	println(<info descr="null"><info descr="GO_LOCAL_VARIABLE">struct1</info></info>.<info descr="null"><info descr="GO_STRUCT_LOCAL_MEMBER">var1</info></info>)
} 