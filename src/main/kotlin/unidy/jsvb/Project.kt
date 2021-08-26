package main.kotlin.unidy.jsvb

import java.io.File
import java.util.regex.Pattern

var loaded = false

class Project(jsvbFile: String, inFile: String, outFile: String) {

    private var jsvbFile: String
    private var inFile: String
    private var outFile: String
    private var inText: String
    private var inList: List<String>
    private var inIndex = 0
    lateinit var outText: String
    private var againFor = false
    private var variables: HashMap<String, Variable> = HashMap()
    private var alternativeNext: HashMap<Int, Int> = HashMap()

    inner class Expression {

        private val sign = Array(128, {
            (it.toChar() == '+' || it.toChar() == '-' || it.toChar() == '*' || it.toChar() == '/'
                    || it.toChar() == '!' || it.toChar() == '(' || it.toChar() == ')' || it.toChar() == '<'
                    || it.toChar() == '>' || it.toChar() == '.' || it.toChar() == '=' || it.toChar() == '%'
                    || it.toChar() == ':' || it.toChar() == ';' || it.toChar() == '?' || it.toChar() == '&'
                    || it.toChar() == '|' || it.toChar() == '^'
                    )
        })
        private val comparison = Array(128, {
            (it.toChar() == '<' || it.toChar() == '>' || it.toChar() == '=' || it.toChar() == ':' || it.toChar() == ';' || it.toChar() == '?')
        })
        private val operatorPriority = Array(128, {
            when (it.toChar()) {
                '^' -> 10
                '*' -> 9
                '/' -> 9
                '%' -> 8
                '+' -> 7
                '-' -> 7
                '>' -> 6
                '<' -> 6
                ':' -> 5
                ';' -> 5
                '=' -> 4
                '?' -> 4
                '!' -> 3
                '&' -> 2
                '|' -> 1
                '(' -> 0
                ')' -> 0
                else -> -1
            }
        })

        private fun transform(expression: String): String {
            val arr = expression.replace(" mod ", "%").replace(">=", ":").replace("<=", ";").replace("<>", "?").replace("not", " not ")
                    .replace(" not ", " ! ").replace(" and ", "&").replace(" or ", "|").replace(" ", "")
                    .toCharArray()
            arr.indices
                    .filter { arr[it] == '-' }
                    .forEach {
                        if (it == 0) {
                            arr[it] = '~'
                        } else {
                            val c = arr[it - 1]
                            if (c == '+' || c == '-' || c == '*' || c == '/' || c == '(') {
                                arr[it] = '~'
                            }
                        }
                    }
            return if ((arr[0] == '~' || arr.size > 1 && arr[1] == '(') && arr[0] != '!') {
                arr[0] = '-'
                "0" + String(arr)
            } else {
                String(arr)
            }
        }

        private fun calculate(a: Variable, b: Variable, c: Char): Variable {
            return when (c) {
                '^' -> a pow b
                '*' -> a * b
                '/' -> a / b
                '%' -> a % b
                '+' -> a + b
                '-' -> a - b
                '>' -> a gt b
                '<' -> a lt b
                ':' -> a ge b
                ';' -> a le b
                '=' -> a eq b
                '?' -> !(a eq b)
                '!' -> !b
                '&' -> a and b
                '|' -> a or b
                else -> throw Exception("Runtime error: invalid character `$c` found.")
            }
        }

        fun expression(expression: String): Variable {
            try {
                val s = "(${transform(expression)})"
                var i = 0
                val c = ArrayList<Char>()
                val d = ArrayList<Variable>()
                var p = 0
                var q = 0
                c.add(' ')
                d.add(Variable())
                while (i < s.length) {
                    if (s[i] in 'a'..'z' || s[i] == '_' || s[i] == '~' && (s[i + 1] in 'a'..'z' || s[i + 1] == '_')) {
                        val t = i
                        while (s[i] != ' ' && !sign[s[i].toInt()]) ++i
                        val id0 = if (s[t] == '~') s.substring(t + 1, i) else s.substring(t, i)
                        val variable = variables[id0] ?: throw Exception("Compilation error: `$id0`is not declared.")
                        d.add(variable)
                        ++q
                    } else if (s[i].isDigit() || s[i] == '~') {
                        val t = i
                        while (s[i].isDigit() || s[i] == '.' || s[i] == '~') {
                            ++i
                        }
                        val variable = Variable()
                        variable.setType("Number")
                        variable.setNumValue(Num(s.substring(t, i).replace('~', '-').toDouble(), true))
                        d.add(variable)
                        ++q
                    } else if (s[i] == '(' || operatorPriority[s[i].toInt()] > operatorPriority[c[p].toInt()] || s[i] == '!' && c[p] == '!') {
                        c.add(s[i])
                        if (s[i] == '!') {
                            d.add(Variable())
                            ++q
                        }
                        ++p
                        ++i
                    } else if (comparison[s[i].toInt()] && comparison[c[p].toInt()] && operatorPriority[s[i].toInt()] == operatorPriority[c[p].toInt()]) {
                        d[q - 1] = calculate(d[q - 1], d[q], c[p])
                        c[p] = '&'
                    } else if (operatorPriority[s[i].toInt()] == -1) {
                        throw Exception("Compilation error: invalid character `${s[i]}` found.")
                    } else {
                        while (operatorPriority[s[i].toInt()] <= operatorPriority[c[p].toInt()]) {
                            if (s[i] == ')' && c[p] == '(') {
                                c.removeAt(p)
                                p -= 1
                                ++i
                                break
                            } else {
                                val digit = calculate(d[q - 1], d[q], c[p])
                                c.removeAt(p)
                                p--
                                d.removeAt(q)
                                q--
                                d[q] = digit
                            }
                        }
                    }
                }
                if (d[1].getType() == "ValuedKey") d[1].setType("Boolean")
                return d[1]
            } catch (e: Exception) {
                e.printStackTrace()
                val result = Variable()
                result.setType("Nothing")
                return result
            }
        }

        inner class Parse {

            private lateinit var lines: List<String>
            private var diagramLines = listOf("")
            private lateinit var diagramFlows: Array<Int>
            private lateinit var alternativeDiagramFlows: Array<Int>

            fun parseCode(str: String) {
                fun f(s: String) =
                        if (s.contains("//"))
                            s.substring(0, s.indexOf("//"))
                        else
                            s
                lines = listOf("") + str.split("\n").map { it -> f(it) }
                when {
                    lines[1].toLowerCase().matches(" *@jsvbcode *".toRegex()) -> {
                        var line = 2
                        while (line < lines.size) {
                            line = parseLine(line)
                        }
                    }
                    lines[1].toLowerCase().matches(" *@jsvbdiagram *".toRegex()) -> {
                        var line = 2
                        var i = 1
                        var haveExit = false
                        while (line < lines.size && lines[line].matches(" *\\d+\\..*".toRegex())) {
                            if (i == lines[line].substring(0, lines[line].indexOf('.')).toInt()) {
                                diagramLines += lines[line].substring(lines[line].indexOf('.') + 1)
                                if (diagramLines[i].toLowerCase().matches(" *exit *".toRegex())) haveExit = true
                                ++line
                                ++i
                            } else {
                                throw Exception("Compilation error: the index of a diagram code should follow the sequence `1,2,3...`. (@line $line)")
                            }
                        }
                        if (!haveExit) throw Exception("Compilation error: expecting code `exit`.")
                        if (line == lines.size || !lines[line].toLowerCase().matches(" *run: *".toRegex()))
                            throw Exception("Compilation error: expecting identifier `Run:` after introducing diagram codes. (@line $line)")
                        var maximum = i - 1
                        diagramFlows = Array(i, { 0 })
                        alternativeDiagramFlows = Array(i, { 0 })
                        ++line
                        i = 1
                        while (line < lines.size-1) {
                            if (lines[line].matches(" *\\d+->\\d+ *".toRegex())) {
                                if (lines[line].substring(0, lines[line].indexOf("->")).replace("^ *".toRegex(), "").replace(" *$".toRegex(), "").toInt() != i)
                                    throw Exception("Compilation error: the left-hand-side of the diagram flow definition should follow the sequence `1,2,3...`. (@line $line)")
                                val right = lines[line].substring(lines[line].indexOf("->") + 2)
                                if (right.matches(" *\\d *".toRegex())) {
                                    diagramFlows[i] = right.replace("^ *".toRegex(), "").replace(" *$".toRegex(), "").toInt()
                                    if (diagramFlows[i] > maximum)
                                        throw Exception("Compilation error. (@line $line)")
                                } else if (right.matches(" *\\( *\\d *, *\\d *\\) *".toRegex())) {
                                    diagramFlows[i] = right.substring(right.indexOf('(') + 1, right.indexOf(',')).replace("^ *".toRegex(), "").replace(" *$".toRegex(), "").toInt()
                                    alternativeDiagramFlows[i] = right.substring(right.indexOf(',') + 1, right.indexOf(')')).replace("^ *".toRegex(), "").replace(" *$".toRegex(), "").toInt()
                                    if (diagramFlows[i] > maximum || alternativeDiagramFlows[i] > maximum)
                                        throw Exception("Compilation error. (@line $line)")
                                }
                            } else {
                                throw Exception("Compilation error: diagram flow shall be defined matching regex `\\d+->\\d+`. (@line $line)")
                            }
                            ++line
                            ++i
                        }
                        lines=diagramLines
                        line=1
                        while(!lines[line].matches(" *exit *".toRegex())){
                            parseLine(line)
                            line=diagramFlows[line]
                        }
                    }
                    else -> throw Exception("Compilation error: the first line of a code should mark its language.\n" +
                            "Change the first line to either `@JSVBCode` or `@JSVBDiagram` instead.")
                }
            }

            private fun parseLine(line: Int): Int {
                try {
                    var s = lines[line]
                    var i = 0
                    Pattern.compile("^( +)").matcher(s).takeIf { it.find() }?.run { i = this.end() }
                    s = s.substring(i)
                    if (s == "") return line + 1
                    Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_]*").matcher(s).takeIf { it.find() }?.run {

                        fun parseAssignment(s: String): Variable {
                            var j = 0
                            Pattern.compile("^( +)").matcher(s).takeIf { it.find() }?.run { j = this.end() }
                            if (s.substring(j, j + 2) == "<-") {
                                return expression(s.substring(j + 2)).takeIf { it.getType() == "Number" || it.getType() == "Boolean" }
                                        ?: throw Exception("Runtime error: type mismatch.")
                            } else {
                                throw Exception("Compilation error: expecting `<-` after an identifier. (@line $line)")
                            }
                        }

                        fun parseRead(s: String) {
                            val memberList = s.toLowerCase().replace(" ", "").split(",".toRegex())
                            var j = 0
                            while (inIndex < inList.size && j < memberList.size) {
                                variables[memberList[j]] = parseAssignment("<-" + inList[inIndex])
                                ++j
                                ++inIndex
                            }
                        }

                        fun parsePrint(s: String) {
                            val memberList = s.toLowerCase().replace(" ", "").split(",".toRegex())
                            var j = 0
                            while (j < memberList.size) {
                                println(expression(memberList[j]))
                                ++j
                            }
                        }

                        fun parseIf(s: String): Int {
                            fun f(s: String): Int {
                                var j = 0
                                Pattern.compile("^( +)").matcher(s).takeIf { it.find() }?.run { j = this.end() }
                                val ex2 = s.substring(j)
                                if (ex2 == "") return line + 1
                                Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_]*").matcher(ex2).takeIf { it.find() }?.run {
                                    val id1 = this.group().toLowerCase()
                                    when (id1) {
                                        "print" -> {
                                            parsePrint(ex2.substring(this.end()))
                                        }
                                        "read" -> {
                                            parseRead(ex2.substring(this.end()))
                                        }
                                        else -> {
                                            with(variables[id1]) {
                                                if (this != null && (this.getType() == "Key" || this.getType() == "ValuedKey"))
                                                    throw Exception("Compilation error: expecting a variable after token `then`. (@line $line)")
                                            }
                                            variables[id1] = parseAssignment(ex2.substring(this.end()).toLowerCase())
                                        }
                                    }
                                    return line + 1
                                }
                                return line + 1
                            }

                            val ex1 = s.toLowerCase().substring(2, s.toLowerCase().indexOf("then"))
                            val condition = expression(ex1).toBool().getBoolValue()
                            if (condition) {
                                s.substring(s.toLowerCase().indexOf("then") + 4).run {
                                    return if (this.matches(" *".toRegex()))
                                        line + 1
                                    else {
                                        val ex2 = if (this.contains("else"))
                                            this.substring(0, this.indexOf("else"))
                                        else
                                            this
                                        f(ex2)
                                    }
                                }
                            } else {
                                s.substring(s.toLowerCase().indexOf("then") + 4).run {
                                    if (this.matches(" *".toRegex())) {
                                        val alternative = alternativeNext[line]
                                        if (alternative == null) {
                                            var j = line + 1
                                            var count = 0
                                            while (count >= 0) {
                                                if (count == 0) {
                                                    if (lines[j].matches(" *end +if *".toRegex()) || lines[j].matches(" *else *".toRegex()) || lines[j].matches(" *else +if *".toRegex())) {
                                                        alternativeNext[line] = j + 1
                                                        return j + 1
                                                    }
                                                }
                                                if (lines[j].matches(" *end +if *".toRegex())) ++count
                                                if (lines[j].matches(" *if +.+ +then *".toRegex())) --count
                                                ++j
                                            }
                                        } else {
                                            return alternative
                                        }
                                    } else {
                                        /*这部分代码在此引用时略去*/
                                        if (this.toLowerCase().contains("else")) {
                                            this.substring(this.toLowerCase().indexOf("else") + 4).run {
                                                var ex2 = this
                                                Pattern.compile(".*end +if").matcher(ex2).takeIf { it.find() }?.run {
                                                    val ex3 = ex2.substring(this.end())
                                                    if (!ex3.matches(" *".toRegex()))
                                                        throw Exception("Compilation error: unexpected token `$ex3` found after `end if`. (@line $line)")
                                                    Pattern.compile("end +if").matcher(ex2).takeIf { it.find() }?.run {
                                                        ex2 = ex2.substring(this.start())
                                                    }
                                                }
                                                return if (this.matches(" *".toRegex()))
                                                    line + 1
                                                else {
                                                    f(this)
                                                }
                                            }
                                        } else {
                                            return line + 1
                                        }
                                    }
                                }
                            }
                            return line + 1

                        }

                        if (this.end() < s.length && s[this.end()] != ' ' && s[this.end()] != '<') throw Exception("Compilation error: unexpected token `${if (s.contains(' ')) s.substring(0, s.indexOf(' ')) else s}`. (@line $line)")
                        val id0 = this.group().toLowerCase()
                        val variableId0 = variables[id0]
                        when {
                            variableId0 == null -> {
                                variables[id0] = parseAssignment(s.substring(this.end()).toLowerCase())
                                return line + 1
                            }
                            variableId0.getType() == "Key" -> {
                                when (id0) {
                                    "if" -> {
                                        return parseIf(s)
                                    }
                                    "else" -> {
                                        if (s.matches("else *".toRegex())) {
                                            val alternative = alternativeNext[line]
                                            if (alternative == null) {
                                                var j = line + 1
                                                var count = 0
                                                while (count >= 0) {
                                                    if (count == 0) {
                                                        if (lines[j].matches(" *end +if *".toRegex())) {
                                                            alternativeNext[line] = j + 1
                                                            return j + 1
                                                        }
                                                    }
                                                    if (lines[j].matches(" *end +if *".toRegex())) ++count
                                                    if (lines[j].matches(" *if +.+ +then *".toRegex())) --count
                                                    ++j
                                                }
                                            } else {
                                                return alternative
                                            }
                                        } else {
                                            s.substring(4).replace("^ +".toRegex(), "").run {
                                                if (this.indexOf("if") == 0)
                                                    return parseIf(this)
                                                else {
                                                    throw Exception("Compilation error: expecting `if` after `else`. (@line $line)")
                                                }
                                            }
                                        }
                                    }
                                    "elseif" -> {
                                        return parseIf(s.substring(4))
                                    }
                                    "end" -> {
                                        return when {
                                            s.matches("end +if *".toRegex()) ->
                                                line + 1
                                            s.matches("end +while *".toRegex()) ->
                                                alternativeNext[line] ?: line+1
                                            s.matches("end +for *".toRegex()) -> {
                                                againFor = true
                                                alternativeNext[line] ?: line+1
                                            }
                                            else ->
                                                throw Exception("Compilation error: unexpected code `$s`. (@line $line)")
                                        }
                                    }
                                    "while" -> {
                                        var alternative = alternativeNext[line]
                                        if (alternative == null) {
                                            var j = line + 1
                                            var count = 0
                                            while (count >= 0) {
                                                if (count == 0) {
                                                    if (lines[j].matches(" *end +while *".toRegex())) {
                                                        alternativeNext[line] = j + 1
                                                        alternativeNext[j] = line
                                                        alternative = j + 1
                                                        break
                                                    }
                                                }
                                                if (lines[j].matches(" *end +while *".toRegex())) ++count
                                                if (lines[j].matches(" *while +.*".toRegex())) --count
                                                ++j
                                            }
                                        }
                                        if (alternative == null) alternative = line + 1
                                        return if (expression(s.substring(5)).toBool().getBoolValue()) {
                                            line + 1
                                        } else {
                                            alternative
                                        }
                                    }
                                    "for" -> {
                                        s = s.toLowerCase()
                                        val id1: String
                                        try {
                                            id1 = s.substring(4, s.indexOf("from")).replace("^ *".toRegex(), "").replace(" *$".toRegex(), "")
                                        } catch (e: Exception) {
                                            throw Exception("Compilation error: expecting `from` after `for`. (@line $line)")
                                        }
                                        if (!id1.matches("[a-zA-Z_][a-zA-Z0-9_]*".toRegex()))
                                            throw Exception("Compilation error: initial variable `$id1` should match regex `[a-zA-Z_][a-zA-Z0-9_]*`. (@line $line)")
                                        s = s.substring(s.indexOf("from") + 4)
                                        if (!s.contains("to"))
                                            throw Exception("Compilation error: expecting `to` after `from`. (@line $line)")
                                        val from = expression(s.substring(0, s.indexOf("to")))
                                        s = s.substring(s.indexOf("to") + 2)
                                        val to: Variable
                                        val step: Variable
                                        if (s.contains("step")) {
                                            to = expression(s.substring(0, s.indexOf("step")))
                                            step = expression(s.substring(s.indexOf("step") + 4))
                                        } else {
                                            to = expression(s)
                                            step = Variable().setType("Number").setNumValue(Num(1))
                                        }
                                        var alternative = alternativeNext[line]
                                        if (alternative == null) {
                                            var j = line + 1
                                            var count = 0
                                            while (count >= 0) {
                                                if (count == 0) {
                                                    if (lines[j].matches(" *end +for *".toRegex())) {
                                                        alternativeNext[line] = j + 1
                                                        alternativeNext[j] = line
                                                        alternative = j + 1
                                                        break
                                                    }
                                                }
                                                if (lines[j].matches(" *end +for *".toRegex())) ++count
                                                if (lines[j].matches(" *for +.*".toRegex())) --count
                                                ++j
                                            }
                                        }
                                        if (alternative == null) alternative = line + 1
                                        if (againFor) {
                                            againFor = false
                                            var variableId1 = variables[id1]
                                                    ?: throw Exception("Runtime error: initial variable `$id1` is not defined. (@line $line)")
                                            variableId1 += step
                                            variables[id1] = variableId1
                                            return if ((from le variableId1).toBool().getBoolValue() && (variableId1 le to).toBool().getBoolValue()) {
                                                line + 1
                                            } else {
                                                alternative
                                            }
                                        } else {
                                            variables[id1] = from
                                            return if ((from le to).toBool().getBoolValue()) {
                                                line + 1
                                            } else {
                                                alternative
                                            }
                                        }
                                    }
                                    "do" -> {
                                        if (s.matches("do *".toRegex())) {
                                            if (alternativeNext[line] == null) {
                                                var j = line + 1
                                                var count = 0
                                                while (count >= 0) {
                                                    if (count == 0) {
                                                        if (lines[j].matches(" *end +do *".toRegex())) {
                                                            alternativeNext[line] = j + 1
                                                            alternativeNext[j - 1] = line
                                                            break
                                                        }
                                                    }
                                                    if (lines[j].matches(" *end +do *".toRegex())) ++count
                                                    if (lines[j].matches(" *do *".toRegex())) --count
                                                    ++j
                                                }
                                            }
                                            return line + 1
                                        }
                                        throw Exception("Compilation error: unexpected token `${s.substring(3)}` after `do`. (@line $line)")
                                    }
                                    "until" -> {
                                        if (!lines[line + 1].matches(" *end +do *".toRegex())) throw Exception("Compilation error: expecting `end do` after `until...`. (@line $line)")
                                        return if (expression(s.substring(5)).toBool().getBoolValue()) {
                                            line + 2
                                        } else {
                                            alternativeNext[line]
                                                    ?: throw Exception("Runtime error. (@line $line)")
                                        }
                                    }
                                    "read" -> {
                                        parseRead(s.substring(this.end()))
                                        return line + 1
                                    }
                                    "print" -> {
                                        parsePrint(s.substring(this.end()))
                                        return line + 1
                                    }
                                    else -> {
                                        throw Exception("Runtime error: unexpected token `$id0`. (@line $line)")
                                    }
                                }
                            }
                            else -> {
                                variables[id0] = parseAssignment(s.substring(this.end()).toLowerCase())
                                return line + 1
                            }
                        }
                    }
                    throw Exception("Compilation error: expecting an identifier at the beginning of a line. (@line $line)")
                } catch (e: Exception) {
                    e.printStackTrace()
                    return line + 1
                }
            }


        }
    }

    init {
        if (!loaded) {
            load()
            loaded = true
        }

        this.jsvbFile = jsvbFile.toLowerCase()
        if (!File(jsvbFile).exists()) {
            throw Exception("Runtime error: jsvbFile $jsvbFile does not exist.")
        }

        this.inFile = inFile.toLowerCase()
        if (inFile != "" && inFile != "console" && !File(inFile).exists()) {
            throw Exception("Runtime error: jsvbFile $inFile does not exist.")
        }
        inText = if (inFile == "console") {
            TODO("Read from console")
        } else if (inFile == "") {
            ""
        } else if (!File(inFile).exists()) {
            throw Exception("Runtime error: jsvbFile $inFile does not exist.")
        } else {
            FileOperation.readFile(inFile)
        }
        inList = inText.split("[, \\n]+".toRegex()).filter { it != "" }

        this.outFile = outFile.toLowerCase()
        if (outFile != "" && outFile != "console" && !File(outFile).exists()) {
            throw Exception("Runtime error: jsvbFile $outFile does not exist.")
        }

    }

    fun getJsvbFile() = jsvbFile
    fun setJsvbFile(jsvbFile: String) {
        this.jsvbFile = jsvbFile
    }

    fun getInFile() = inFile
    fun setInFile(inFile: String) {
        this.inFile = inFile
    }

    fun getOutFile() = outFile
    fun setOutFile(outFile: String) {
        this.outFile = outFile
    }

    fun getVariables() = variables
    fun setVariables(variables: HashMap<String, Variable>) {
        this.variables = variables
    }

    private fun load() {
        fun setKey() {
            val key0 = Variable()
            key0.setType("Key")
            variables["if"] = key0
            variables["then"] = key0
            variables["else"] = key0
            variables["elseif"] = key0
            variables["end"] = key0
            variables["for"] = key0
            variables["from"] = key0
            variables["to"] = key0
            variables["step"] = key0
            variables["do"] = key0
            variables["while"] = key0
            variables["until"] = key0
            variables["break"] = key0
            variables["import"] = key0
            variables["print"] = key0
            variables["read"] = key0
            variables["mod"] = key0
            val key1 = Variable()
            key1.setType("ValuedKey")
            key1.setBoolValue(false)
            variables["false"] = key1
            val key2 = Variable()
            key2.setType("ValuedKey")
            key2.setBoolValue(true)
            variables["true"] = key2
        }
        setKey()
    }


    fun execute() {
        this.Expression().Parse().parseCode(FileOperation.readFile(jsvbFile))
    }
}