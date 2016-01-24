package main

type <info descr="null"><info descr="GO_PACKAGE_LOCAL_INTERFACE">inner</info></info> interface {
	<info descr="null"><info descr="GO_EXPORTED_FUNCTION">Inner</info></info>() <info descr="null"><info descr="GO_BUILTIN_TYPE_REFERENCE">string</info></info>
}

type (
	<info descr="null"><info descr="GO_PACKAGE_LOCAL_INTERFACE">de</info></info> interface {
		<info descr="null"><info descr="GO_EXPORTED_FUNCTION">Demo</info></info>() <info descr="null"><info descr="GO_PACKAGE_LOCAL_INTERFACE">inner</info></info>
	}

	<info descr="null"><info descr="GO_PACKAGE_LOCAL_STRUCT">dem</info></info> struct{}
)

func (<info descr="null"><info descr="GO_METHOD_RECEIVER">a</info></info> <info descr="null"><info descr="GO_TYPE_REFERENCE">dem</info></info>) <info descr="null"><info descr="GO_EXPORTED_FUNCTION">Demo</info></info>() <info descr="null"><info descr="GO_PACKAGE_LOCAL_INTERFACE">inner</info></info> {
	return <info descr="null"><info descr="GO_BUILTIN_TYPE_REFERENCE">error</info></info>("demo")
}

func <info descr="null"><info descr="GO_LOCAL_FUNCTION">main</info></info>() {
	<info descr="null"><info descr="GO_LOCAL_VARIABLE">b</info></info> := <info descr="null"><info descr="GO_PACKAGE_LOCAL_STRUCT">dem</info></info>{}
	<info descr="null"><info descr="GO_LOCAL_VARIABLE">b</info></info>.<info descr="null"><info descr="GO_EXPORTED_FUNCTION">Demo</info></info>().<info descr="null"><info descr="GO_EXPORTED_FUNCTION">Inner</info></info>()
}