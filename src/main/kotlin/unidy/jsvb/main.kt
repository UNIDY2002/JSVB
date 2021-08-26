package main.kotlin.unidy.jsvb

import main.kotlin.unidy.jsvb.grammar.TestParser
import net.percederberg.grammatica.Grammar
import net.percederberg.grammatica.output.JavaParserGenerator
import java.io.File

/* fun main() {
    val grammar = Grammar(File("test.file/test.grammar"))
    val generator = JavaParserGenerator(grammar)
    generator.baseDir = File("src")
    generator.baseName = "Test"
    generator.basePackage = "main.kotlin.unidy.jsvb.grammar"
    generator.publicAccess = true
    generator.write()
    val result= TestParser("if 1+1=2 then a<-1+1 else a<-1*1".reader()).parse()
} */

fun main() {
    try {
        Project(jsvbFile = "test.file/test.jsvb",
                inFile = "test.file/test.in",
                outFile = "test.file/test.out").execute()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}