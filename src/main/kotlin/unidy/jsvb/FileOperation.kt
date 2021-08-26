package main.kotlin.unidy.jsvb

import java.io.*

object FileOperation {

    fun readFile(fileName: File): String {
        val reader = BufferedReader(InputStreamReader(FileInputStream(fileName), "UTF-8"))
        try {
            var result = ""
            var t: String? = reader.readLine()
            while (t != null) {
                result = result + t + "\n"
                t = reader.readLine()
            }
            reader.close()
            return result
        }catch (e:Exception){
            reader.close()
            e.printStackTrace()
            throw Exception("Runtime error: file ${fileName.name} cannot be read.")
        }
    }

    fun readFile(fileName:String)= readFile(File(fileName))

    fun writeFile(content: String, fileName: File): Boolean {
        var flag = false
        val o: FileOutputStream
        try {
            o = FileOutputStream(fileName)
            o.write(content.toByteArray(charset("UTF-8")))
            o.close()
            flag = true
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return flag
    }
}