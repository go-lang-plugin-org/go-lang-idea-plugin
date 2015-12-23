package main

type (
	<info descr="null"><info descr="GO_PACKAGE_EXPORTED_INTERFACE">PublicInterface</info></info>  interface {
		<info descr="null"><info descr="GO_EXPORTED_FUNCTION">PublicFunc</info></info>() int
		<info descr="null"><info descr="GO_LOCAL_FUNCTION">privateFunc</info></info>() int
	}

	<info descr="null"><info descr="GO_PACKAGE_LOCAL_INTERFACE">private</info></info> interface {
		<info descr="null"><info descr="GO_EXPORTED_FUNCTION">PublicFunc</info></info>() int
		<info descr="null"><info descr="GO_LOCAL_FUNCTION">privateFunc</info></info>() int
	}

	<info descr="null"><info descr="GO_PACKAGE_EXPORTED_STRUCT">PublicStruct</info></info> struct {

		<info descr="null"><info descr="GO_STRUCT_EXPORTED_MEMBER">PublicField</info></info>  int
		<info descr="null"><info descr="GO_STRUCT_LOCAL_MEMBER">privateField</info></info> int
	}

	<info descr="null"><info descr="GO_PACKAGE_LOCAL_STRUCT">privateStruct</info></info> struct {
		<info descr="null"><info descr="GO_STRUCT_EXPORTED_MEMBER">PublicField</info></info>  int
		<info descr="null"><info descr="GO_STRUCT_LOCAL_MEMBER">privateField</info></info> int
	}

	<info descr="null"><info descr="GO_TYPE_SPECIFICATION">demoInt</info></info> int
)