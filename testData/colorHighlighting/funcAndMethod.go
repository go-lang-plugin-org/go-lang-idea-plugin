package main

type <info descr="null"><info descr="type">inner</info></info> interface {
	<info descr="null"><info descr="signature_owner">Inner</info></info>() string
}

type (
	<info descr="null"><info descr="type">de</info></info> interface {
		<info descr="null"><info descr="signature_owner">Demo</info></info>() <info descr="null"><info descr="type">inner</info></info>
	}

	<info descr="null"><info descr="type">dem</info></info> struct{}
)

func (<info descr="null"><info descr="receiver">a</info></info> <info descr="null"><info descr="type">dem</info></info>) <info descr="null"><info descr="signature_owner">Demo</info></info>() <info descr="null"><info descr="type">inner</info></info> {
	return error("demo")
}

func <info descr="null"><info descr="signature_owner">main</info></info>() {
	<info descr="null"><info descr="var">b</info></info> := <info descr="null"><info descr="type">dem</info></info>{}
	<info descr="null"><info descr="var">b</info></info>.<info descr="null"><info descr="func">Demo</info></info>().<info descr="null"><info descr="func">Inner</info></info>()
}