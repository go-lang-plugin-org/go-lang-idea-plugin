package hello_test

import . "gopkg.in/check.v1"

<caret>

type MySuite struct{}
var _ = Suite(&MySuite{})
func (s *MySuite) TestHelloWorld(c *C) {

}