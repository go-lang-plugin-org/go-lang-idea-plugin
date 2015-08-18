package main

func Test(/*def*/p interface{}) error {
	switch p := /*ref*/p.(type) {
		case error:
		return nil
	}
	return nil
}