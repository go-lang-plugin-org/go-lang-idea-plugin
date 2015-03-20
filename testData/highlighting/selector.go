package main

import "<error>db</error>"

type search struct {
    generals []string
}

type context struct {
    Db db.<error>Database</error>
}

func <warning>initDb</warning>(c context) {
    <error>_</error> := c.Db.<error>search</error>
}