package demo

func _() {
	<weak_warning descr="defer should not call recover() directly">defer recover()</weak_warning>
	<weak_warning descr="defer should not call panic() directly">defer panic("ads")</weak_warning>
	defer <error descr="defer requires function call, not conversion">int(0)</error>


	<weak_warning descr="go should not call recover() directly">go recover()</weak_warning>
	<weak_warning descr="go should not call panic() directly">go panic("ads")</weak_warning>
	go <error descr="go requires function call, not conversion">int(0)</error>
}
