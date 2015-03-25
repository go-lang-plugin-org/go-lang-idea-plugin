// DO NOT EDIT. THIS IS FILE IS GENERATED AUTOMAGICALLY. ANY CHANGE WILL BE LOST.

package cli

import (
        "bytes"
        "encoding/json"
        "errors"
        "fmt"
        "io"
        "net"
        "net/textproto"
        "strings"
        "sync/atomic"
        "text/template"
)

type session struct {
        *textproto.Conn
        netConn    net.Conn
        lineBuffer bytes.Buffer
        requestId  uint32
        sessionId  uint32
        outType    int
        closed     bool
}

type θ_parse_node struct {
        name     string
        ident    bool
        bindings []int
        action   func(*session, []string) error
        doc      string
        next     []θ_parse_node
}


func (s *session) θ_echo(p []string) (err error) {
        ret := echo(p[0])
        err = s.θ_writeResponse(ret, nil)
        if err != nil {
                return err
        } else {
                return s.θ_writeOk()
        }
}
func (s *session) θ_get_server_time(p []string) (err error) {
        ret := get_server_time()
        err = s.θ_writeResponse(ret, nil)
        if err != nil {
                return err
        } else {
                return s.θ_writeOk()
        }
}
func (s *session) θ_get_server_time(p []string) (err error) {
        ret := get_server_time()
        err = s.θ_writeResponse(ret, nil)
        if err != nil {
                return err
        } else {
                return s.θ_writeOk()
        }
}
func (s *session) θ_debug_ide(p []string) (err error) {
        p0, err := s.parse_number(p[0])
        if err != nil {
                return s.θ_writeError(err)
        }
        ret := debug_ide(p0)
        err = s.θ_writeResponse(ret, nil)
        if err != nil {
                return err
        } else {
                return s.θ_writeOk()
        }
}

var θ_parse_tree = θ_parse_node{"", true, []int{}, nil, "", []θ_parse_node{θ_parse_node{"debug", true, []int{}, nil, "", []θ_parse_node{θ_parse_node{"idea", true, []int{}, nil, "", []θ_parse_node{θ_parse_node{"patience", true, []int{}, nil, "", []θ_parse_node{θ_parse_node{"for", true, []int{}, nil, "", []θ_parse_node{θ_parse_node{"tree", true, []int{}, nil, "", []θ_parse_node{θ_parse_node{"with", true, []int{}, nil, "", []θ_parse_node{θ_parse_node{"nested", true, []int{}, nil, "", []θ_parse_node{θ_parse_node{"level", true, []int{}, nil, "", []θ_parse_node{θ_parse_node{"more", true, []int{}, nil, "", []θ_parse_node{θ_parse_node{"than", true, []int{}, nil, "", []θ_parse_node{θ_parse_node{"<number>", false, []int{0}, (*session).θ_debug_ide, "here goes an unnecessary long command", []θ_parse_node{}}}}}}}}}}}}}}}}}}}}}}, θ_parse_node{"echo", true, []int{}, nil, "", []θ_parse_node{θ_parse_node{"<string>", false, []int{0}, (*session).θ_echo, "Returns its parameter", []θ_parse_node{}}}}, θ_parse_node{"get", true, []int{}, nil, "", []θ_parse_node{θ_parse_node{"output", true, []int{}, nil, "", []θ_parse_node{θ_parse_node{"type", true, []int{}, (*session).θ_get_output_type, "returns current output type", []θ_parse_node{}}}}, θ_parse_node{"server", true, []int{}, nil, "", []θ_parse_node{θ_parse_node{"time", true, []int{}, (*session).θ_get_server_time, "Returns server time", []θ_parse_node{}}}}}}, θ_parse_node{"quit", true, []int{}, (*session).θ_quit, "closes CLI connection", []θ_parse_node{}}, θ_parse_node{"set", true, []int{}, nil, "", []θ_parse_node{θ_parse_node{"output", true, []int{}, nil, "", []θ_parse_node{θ_parse_node{"type", true, []int{}, nil, "", []θ_parse_node{θ_parse_node{"to", true, []int{}, nil, "", []θ_parse_node{θ_parse_node{"<string>", false, []int{0}, (*session).θ_set_output_type, "sets current output type (either json or text)", []θ_parse_node{}}}}}}}}}}, θ_parse_node{"show", true, []int{}, nil, "", []θ_parse_node{θ_parse_node{"server", true, []int{}, nil, "", []θ_parse_node{θ_parse_node{"time", true, []int{}, (*session).θ_get_server_time, "Returns server time", []θ_parse_node{}}}}}}}}


type stru struct {
    Field string
    Field1 map[string]string
}

type str struct {
    Fiel *stru
}

func de() *stru {
    a := &stru{
        Field: "demo",
        Field1: map[string]string{"f1": "val"},
    }

    a.Field = "dem"
    a.Field1 = map[string]string{"f2": "val"}

    return a
}

func dem() {
    b := de()
    b.Field = "de"
    b.Field1 = map[string]string{"f3": "fal"}

    a := *str{
        Fiel: &stru{
            Field: fmt.Sprint("demo%d", 1),
            Field1: map[string]string{"a", "n"},
        },
    }

    for i := 0; i<4; i++ {
        a.Fiel = &stru{
            Field: fmt.Sprintf("demo %d", i), // parser fails here
            Field1: map[string]string{
                "a",
                "n",
            },
        }
    }

    _ = b
    _ = a
}