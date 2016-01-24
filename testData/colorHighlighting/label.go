package main

func <info descr="null"><info descr="GO_LOCAL_FUNCTION">test</info></info>() <info descr="null"><info descr="GO_BUILTIN_TYPE_REFERENCE">int</info></info> {
<info descr="null"><info descr="GO_LABEL">foo</info></info>:
	for {
		continue <info descr="null"><info descr="GO_LABEL">foo</info></info>
		break <info descr="null"><info descr="GO_LABEL">foo</info></info>
		goto <info descr="null"><info descr="GO_LABEL">foo</info></info>
	}
	<info descr="null"><info descr="GO_LOCAL_VARIABLE">foo</info></info> := 10
	return <info descr="null"><info descr="GO_LOCAL_VARIABLE">foo</info></info>
}
