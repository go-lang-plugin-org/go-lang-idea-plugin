package main
type myHandler struct {
handlers map[string]func(
    w http.ResponseWriter,
    r *http.Request,
    queues *yqs.Queues) (int)
templates map[string]*template.Template
}

