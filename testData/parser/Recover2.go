package http

func labelsCheck() { goto Label1; Label1: 1; goto Label2}

func serve() {
    foo(
    if 1 {}
}

func foo() {
    bar(,)
    doo()
}

func main() {
    switch expr {
        case
    }
}

func _() {
    []string
}

func User(createBase base.AuthFunc) base.Ajaxer {

	log.Println("Restful user request.")
	authCtrl := createBase("controller/account/user")

	switch authCtrl.Req.Method {
	case "PUT":
		return &edit{AuthCtrl: authCtrl}
		"DELETE":
		return &delete{AuthCtrl: authCtrl}
	default:
		return &read{AuthCtrl: authCtrl}
	}
}

func User(createBase base.AuthFunc) base.Ajaxer {

	log.Println("Restful user request.")
	authCtrl := createBase("controller/account/user")

	switch authCtrl.Req.Method {
	"PUT":
		return &edit{AuthCtrl: authCtrl}
		"DELETE":
		return &delete{AuthCtrl: authCtrl}
	default:
		return &read{AuthCtrl: authCtrl}
	}
}