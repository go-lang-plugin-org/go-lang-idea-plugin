package test

/*
#cgo LDFLAGS: -L${SRCDIR}/libs -lopus

#include "include/opus.h"
 */
import "C"
import "package/path"

func hello() string {
  return "hello"
}