// Copyright 2009 The Go Authors. All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.

// HTTP server.  See RFC 2616.

package http


func (sh serverHandler) ServeHTTP(rw ResponseWriter, req *Request) {
  for {
        w, err := c.readRequest()
  }
  
  for atomic.LoadUint32(&x) != expected {
  
  }
  
  for ; ; c <- *a[S{}.i] {
          if t != nil {
                  break
          }
  }
  
  if req.RequestURI == "*" && req.Method == "OPTIONS" {
        handler = globalOptionsHandler{}
  }
  
  for {
          handler = globalOptionsHandler{}
  }
  
  if err := bar(func() int {return 42}); err != nil {
        panic(err)
  }
  
  type M2 struct { name string; want int }

  for n, tt := range []M2{
      {"Alice", 1}, 
      {"Bob", 2},
      {"Chris", 3},
  } {
      fmt.Print(n, tt)
  }
}

func ValidName(name string) (string, error) {
    if kind, ok := map[string]string{
        "basic":  "Basic",
        "active": "Active",
    }[strings.ToLower(name)]; ok {
        return kind, nil
    } else {
        return "", fmt.Errorf("Invalid name: %s", name)
    }
}