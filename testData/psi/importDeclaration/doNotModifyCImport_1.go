package test

/*
#cgo LDFLAGS: -L${SRCDIR}/libs -lopus

#include "include/opus.h"
 */
import "C"

func hello() string {
  return "hello"
}