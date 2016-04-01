package main

func _() {
    select {
    //noinspection GoUnresolvedReference
    case foo <- bar:
      select {
      case foo <- bar:
        break
      }
    }
    
    
    select {
    //noinspection GoUnresolvedReference
    case foo <- bar:
      select {
      case foo <- bar:
        break
      }
    }
}