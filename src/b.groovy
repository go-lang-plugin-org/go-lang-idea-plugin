
/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Jul 25, 2010
 * Time: 10:00:31 PM
 * To change this template use File | Settings | File Templates.
 */
dirName = "C:/MyPictures/Pic0001"
new File(dirName).eachFile() { file ->
    def newName = (file.getName() =~ /.jpg/).replaceFirst("0001.jpg")
    File f = new File(dirName + "/" + newName)
    file.renameTo(f)
    println file.getName() + " -> " + f.getName()
}