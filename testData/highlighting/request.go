package foo

import "net/http"

type Request interface {
    GetHttpRequest() *http.Request
}

func _(r *http.Request) {
    print(r.URL.Path)
}