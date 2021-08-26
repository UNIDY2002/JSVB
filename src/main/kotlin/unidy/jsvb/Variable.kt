package main.kotlin.unidy.jsvb

class Variable {
    private var index = -1
    private var type = "Nothing"
    private var numValue = Num(0)
    private var boolValue = false

    fun getIndex() = index
    fun setIndex(index: Int):Variable {
        this.index = index
        return this
    }

    fun getType() = type
    fun setType(type: String):Variable {
        this.type = type
        return this
    }

    fun getNumValue() = numValue
    fun setNumValue(numValue: Num):Variable {
        this.numValue = numValue
        return this
    }

    fun getBoolValue() = boolValue
    fun setBoolValue(boolValue: Boolean):Variable {
        this.boolValue = boolValue
        return this
    }

    fun setBoolValue():Variable {
        boolValue = !boolValue
        return this
    }

    fun toNum(): Variable {
        when (type) {
            "Boolean" -> {
                this.setType("Number")
                this.setNumValue(Num(boolValue))
            }
            "Number" -> {

            }
            else -> {
                throw Exception("Runtime error: cannot convert type `$type` to `Num`.")
            }
        }
        return this
    }

    fun toBool(): Variable {
        when (type) {
            "Boolean" -> {

            }
            "Number" -> {
                this.setType("Boolean")
                this.setBoolValue(numValue.value() == 1.0)
            }
            else -> {
                throw Exception("Runtime error: cannot convert type `$type` to `Bool`.")
            }
        }
        return this
    }

    override fun toString(): String {
        return when (type) {
            "Number" -> {
                numValue.toString()
            }
            "Boolean" -> {
                boolValue.toString()
            }
            else -> "Error!"
        }
    }

    operator fun plus(other: Variable): Variable {
        if (this.getType() == "ValuedKey") this.setType("Boolean")
        if (other.getType() == "ValuedKey") other.setType("Boolean")
        this.toNum()
        other.toNum()
        val result = Variable()
        result.setType("Number")
        result.setNumValue(this.getNumValue() + other.getNumValue())
        return result
    }

    operator fun minus(other: Variable): Variable {
        if (this.getType() == "ValuedKey") this.setType("Boolean")
        if (other.getType() == "ValuedKey") other.setType("Boolean")
        this.toNum()
        other.toNum()
        val result = Variable()
        result.setType("Number")
        result.setNumValue(this.getNumValue() - other.getNumValue())
        return result
    }

    operator fun times(other: Variable): Variable {
        if (this.getType() == "ValuedKey") this.setType("Boolean")
        if (other.getType() == "ValuedKey") other.setType("Boolean")
        this.toNum()
        other.toNum()
        val result = Variable()
        result.setType("Number")
        result.setNumValue(this.getNumValue() * other.getNumValue())
        return result
    }

    operator fun div(other: Variable): Variable {
        if (this.getType() == "ValuedKey") this.setType("Boolean")
        if (other.getType() == "ValuedKey") other.setType("Boolean")
        this.toNum()
        other.toNum()
        val result = Variable()
        result.setType("Number")
        result.setNumValue(this.getNumValue() / other.getNumValue())
        return result
    }

    operator fun rem(other: Variable): Variable {
        if (this.getType() == "ValuedKey") this.setType("Boolean")
        if (other.getType() == "ValuedKey") other.setType("Boolean")
        this.toNum()
        other.toNum()
        val result = Variable()
        result.setType("Number")
        result.setNumValue(this.getNumValue() % other.getNumValue())
        return result
    }

    infix fun pow(other: Variable): Variable {
        if (this.getType() == "ValuedKey") this.setType("Boolean")
        if (other.getType() == "ValuedKey") other.setType("Boolean")
        this.toNum()
        other.toNum()
        val result = Variable()
        result.setType("Number")
        result.setNumValue(this.getNumValue() pow other.getNumValue())
        return result
    }

    operator fun not(): Variable {
        if (this.getType() == "ValuedKey") this.setType("Boolean")
        this.toBool()
        val result = this
        result.setBoolValue()
        return result
    }

    infix fun eq(other: Variable): Variable {
        if (this.getType() == "ValuedKey") this.setType("Boolean")
        if (other.getType() == "ValuedKey") other.setType("Boolean")
        this.toNum()
        other.toNum()
        val result = Variable()
        result.setType("Boolean")
        result.setBoolValue(this.getNumValue().value() == other.getNumValue().value())
        return result
    }

    infix fun and(other: Variable): Variable {
        if (this.getType() == "ValuedKey") this.setType("Boolean")
        if (other.getType() == "ValuedKey") other.setType("Boolean")
        this.toBool()
        other.toBool()
        val result = Variable()
        result.setType("Boolean")
        result.setBoolValue(boolValue && other.getBoolValue())
        return result

    }

    infix fun or(other: Variable): Variable {
        if (this.getType() == "ValuedKey") this.setType("Boolean")
        if (other.getType() == "ValuedKey") other.setType("Boolean")
        this.toBool()
        other.toBool()
        val result = Variable()
        result.setType("Boolean")
        result.setBoolValue(boolValue || other.getBoolValue())
        return result

    }

    infix fun gt(other: Variable): Variable {
        if (this.getType() == "ValuedKey") this.setType("Boolean")
        if (other.getType() == "ValuedKey") other.setType("Boolean")
        this.toNum()
        other.toNum()
        val result = Variable()
        result.setType("Boolean")
        result.setBoolValue(this.getNumValue().value() > other.getNumValue().value())
        return result

    }

    infix fun lt(other: Variable): Variable {
        if (this.getType() == "ValuedKey") this.setType("Boolean")
        if (other.getType() == "ValuedKey") other.setType("Boolean")
        this.toNum()
        other.toNum()
        val result = Variable()
        result.setType("Boolean")
        result.setBoolValue(this.getNumValue().value() < other.getNumValue().value())
        return result

    }

    infix fun ge(other: Variable): Variable {
        if (this.getType() == "ValuedKey") this.setType("Boolean")
        if (other.getType() == "ValuedKey") other.setType("Boolean")
        this.toNum()
        other.toNum()
        val result = Variable()
        result.setType("Boolean")
        result.setBoolValue(this.getNumValue().value() >= other.getNumValue().value())
        return result

    }

    infix fun le(other: Variable): Variable {
        if (this.getType() == "ValuedKey") this.setType("Boolean")
        if (other.getType() == "ValuedKey") other.setType("Boolean")
        this.toNum()
        other.toNum()
        val result = Variable()
        result.setType("Boolean")
        result.setBoolValue(this.getNumValue().value() <= other.getNumValue().value())
        return result

    }

}