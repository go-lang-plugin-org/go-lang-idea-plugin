package mongo

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