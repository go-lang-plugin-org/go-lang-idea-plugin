package mongo

import "fmt"

type Collection struct{}
type CollectionLoaderInterface interface {
    MangoCollection(string) *Collection
}

func <warning descr="Unused function 'Collection'">Collection</warning>(parent interface{},collection string) *Collection{
     switch parent := parent.(type) {
          case CollectionLoaderInterface:
          return parent.MangoCollection(collection)
     }
     return nil
}

func <warning>main1</warning>(err error) {
    switch err.(type) {
        case nil: return
    }
}

type advSearch struct {
    Genres struct {
        Generals  []string
        Thematics struct {
            Genres  []string
            Missing bool
        }
        Demographics struct {
            Genres  []string
            Missing bool
        }
    }
}

func <warning>search</warning>() bool {
    m := advSearch{}
    return m.Genres.Demographics.Missing
}


func g(a, b, c int) (int, int) {
    return a, b
}

func f(a, b int) {
}

func <warning>test</warning>() {
    f(g(1, 2, 3))
}

func <warning>test2</warning>() (bool, string) {
    ch := make(chan string)
    var str string
    var isOpen bool
    select {
    case str = <-ch :
    case str, isOpen = <-ch:
    case s, ok := <-ch:
        str = s
        isOpen = ok
    case s := <-ch:
        str = s
    }
    return isOpen, str
}


func <warning>Test23</warning>() (err error) {
    var c chan int
    select {
    case err := (<-c): // err declared and not used
    }
    return err
}

func Demo() error {
    return fmt.Errorf("err")
}

func <warning>main</warning>() {
    var err error

    switch  {
    case 1==2:
        err := Demo()
        panic(err)
    default:
        err = Demo()
        panic(err)
    }
    //panic(err)
}