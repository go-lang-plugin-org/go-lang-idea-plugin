package main

func main() {
	map1 := map[string]int{}
	/*begin*/delete()/*end.missing arguments to delete*/
	/*begin*/delete(1)/*end.missing second (key) argument to delete*/
	delete(map1, "key1", /*begin*/"key"/*end.extra argument to delete*/)
	delete(map1, "key")
	delete(map1, /*begin*/1/*end.cannot use 1 (type int) as type string in delete|CastTypeFix*/)
}
