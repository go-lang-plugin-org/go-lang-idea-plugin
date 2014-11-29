package foo

import "net/http"

type Request interface {
    GetHttpRequest() *http.Request
}