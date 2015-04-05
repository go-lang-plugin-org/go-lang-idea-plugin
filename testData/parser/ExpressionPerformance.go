package performance
import (
	"testing"
	. "github.com/smartystreets/goconvey/convey"
)

func TestFoo(t *testing.T) {
	Convey("All starts with 1", t, func() {
		So(Foo(), ShouldEqual, 1)
		Convey("Then it increases", func() {
			So(Foo(), ShouldEqual, 2)
			Convey("Then it increases", func() {
				So(Foo(), ShouldEqual, 3)
				Convey("Then it increases", func() {
					So(Foo(), ShouldEqual, 4)
					Convey("Then it increases", func() {
						So(Foo(), ShouldEqual, 5)
						Convey("Then it increases", func() {
							So(Foo(), ShouldEqual, 6)
							Convey("Then it increases", func() {
								So(Foo(), ShouldEqual, 7)
								Convey("Then it increases", func() {
									So(Foo(), ShouldEqual, 8)
									Convey("Then it stops", func() {
										So(Foo(), ShouldEqual, func() {



										} )
									})
								})
							})
						})
					})
				})
			})
		})

	})
}