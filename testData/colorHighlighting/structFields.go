package main

type <info descr="null"><info descr="GO_PACKAGE_LOCAL_STRUCT">type1</info></info> struct {
	<info descr="null"><info descr="GO_STRUCT_LOCAL_MEMBER">var1</info></info> <info descr="null"><info descr="GO_BUILTIN_TYPE_REFERENCE">int</info></info>
}

func <info descr="null"><info descr="GO_LOCAL_FUNCTION">main</info></info>() {
	<info descr="null"><info descr="GO_LOCAL_VARIABLE">struct1</info></info> := <info descr="null"><info descr="GO_PACKAGE_LOCAL_STRUCT">type1</info></info>{<info descr="null"><info descr="GO_STRUCT_LOCAL_MEMBER">var1</info></info>: 1}
	<info descr="null"><info descr="GO_BUILTIN_FUNCTION">println</info></info>(<info descr="null"><info descr="GO_LOCAL_VARIABLE">struct1</info></info>.<info descr="null"><info descr="GO_STRUCT_LOCAL_MEMBER">var1</info></info>)
} 