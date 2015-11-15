package main

import `os`

func _() {
  var file, _ = os.Open("name")
  file.Na<caret>me()
}