package main.kotlin.unidy.jsvb

import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.log2

class Num {
    private var upper: Long
    private var lower: Long = 1L
    private var decimal: Double = 0.0
    private var accuracy = true

    fun getUpper() = upper
    fun setUpper(upper: Long) {
        this.upper = upper
    }

    fun getLower() = lower
    fun setLower(lower: Long) {
        this.lower = lower
    }

    fun setDecimal(decimal: Double) {
        this.decimal = decimal
    }

    fun getAccuracy() = accuracy
    fun setAccuracy() {
        accuracy = !accuracy
    }

    fun setAccuracy(accuracy: Boolean) {
        this.accuracy = accuracy
    }

    constructor(value: Int) {
        upper = value.toLong()
    }

    constructor(value: Long) {
        upper = value
    }

    constructor(value: Double) {
        accuracy = false
        upper = 0
        decimal = value
    }

    constructor(value: Boolean) {
        upper = if (value) 1 else 0
    }

    constructor(value: Double, accuracy: Boolean) {
        if (accuracy) {
            var upper = value
            this.accuracy = true
            while (upper.toLong().toDouble() != upper) {
                if (log10(abs(upper)) + 1 > log10(Long.MAX_VALUE.toDouble())) {
                    this.accuracy = false
                    this.upper = 0
                    decimal = value
                    return
                }
                lower *= 10
                upper *= 10
            }
            this.upper = upper.toLong()
            this.simplify()
        } else {
            this.accuracy = false
            upper = 0
            decimal = value
        }
    }

    override fun toString(): String {
        return if(accuracy && upper%lower!=0L)
            "$upper/$lower"
        else if(accuracy && upper%lower==0L)
            (upper/lower).toString()
        else
            value().toString()
    }

    private fun gcd(a: Long, b: Long): Long = if (b == 0L) a else gcd(abs(b), abs(a) % abs(b))

    private fun simplify() {
        if (upper == 0L) {
            lower = 1
        } else {
            val gcd = gcd(upper, lower)
            upper /= gcd
            lower /= gcd
        }
    }

    fun value() = if (accuracy) upper.toDouble() / lower.toDouble() else decimal

    operator fun plus(other: Num): Num {
        return if (this.getAccuracy() && other.getAccuracy()) {
            val result = Num(0)
            if (log2(abs(this.getUpper()).toDouble()) + log2(abs(other.getLower()).toDouble()) > log2(Long.MAX_VALUE.toDouble())
                    || log2(abs(other.getUpper()).toDouble()) + log2(abs(this.getLower()).toDouble()) > log2(Long.MAX_VALUE.toDouble())
                    || log2(abs(this.getLower()).toDouble()) + log2(abs(other.getLower()).toDouble()) > log2(Long.MAX_VALUE.toDouble())) {
                result.setAccuracy()
                result.setDecimal(this.value() + other.value())
                result
            } else {
                result.setUpper(this.getUpper() * other.getLower() + this.getLower() * other.getUpper())
                result.setLower(this.getLower() * other.getLower())
                result.simplify()
                result
            }
        } else
            Num(this.value() + other.value())
    }

    operator fun minus(other: Num): Num {
        return if (this.getAccuracy() && other.getAccuracy()) {
            val result = Num(0)
            if (log2(abs(this.getUpper()).toDouble()) + log2(abs(other.getLower()).toDouble()) > log2(Long.MAX_VALUE.toDouble())
                    || log2(abs(other.getUpper()).toDouble()) + log2(abs(this.getLower()).toDouble()) > log2(Long.MAX_VALUE.toDouble())
                    || log2(abs(this.getLower()).toDouble()) + log2(abs(other.getLower()).toDouble()) > log2(Long.MAX_VALUE.toDouble())) {
                result.setAccuracy()
                result.setDecimal(this.value() - other.value())
                result
            } else {
                result.setUpper(this.getUpper() * other.getLower() - this.getLower() * other.getUpper())
                result.setLower(this.getLower() * other.getLower())
                result.simplify()
                result
            }
        } else
            Num(this.value() - other.value())
    }

    operator fun times(other: Num): Num {
        return if (this.getAccuracy() && other.getAccuracy()) {
            val result = Num(0)
            if (log2(abs(this.getUpper()).toDouble()) + log2(abs(other.getUpper()).toDouble()) > log2(Long.MAX_VALUE.toDouble())
                    || log2(abs(this.getLower()).toDouble()) + log2(abs(other.getLower()).toDouble()) > log2(Long.MAX_VALUE.toDouble())) {
                result.setAccuracy()
                result.setDecimal(this.value() * other.value())
                result
            } else {
                result.setUpper(this.getUpper() * other.getUpper())
                result.setLower(this.getLower() * other.getLower())
                result.simplify()
                result
            }
        } else
            Num(this.value() * other.value())
    }

    operator fun div(other: Num): Num {
        if (other.getAccuracy()) {
            if (other.getUpper() == 0L)
                throw Exception("Runtime error: divided by zero.")
            val t = other.getUpper()
            other.setUpper(other.getLower())
            other.setLower(t)
        } else {
            if (other.value() == 0.0)
                throw Exception("Runtime error: divided by zero.")
            other.setDecimal(other.value())
        }
        return this.times(other)
    }

    operator fun rem(other: Num): Num {
        if (this.value().toLong().toDouble() != this.value() || other.value().toLong().toDouble() != other.value())
            throw Exception("Runtime error: `mod` operation can only be conducted between integers.")
        return Num(this.value().toLong() % other.value().toLong())
    }

    infix fun pow(other: Num): Num {
        return if (this.getAccuracy()) {
            this.simplify()
            val result = Num(0)
            if (log2(abs(this.getUpper()).toDouble()) * other.value() > log2(Long.MAX_VALUE.toDouble())
                    || log2(abs(this.getLower()).toDouble()) * other.value() > log2(Long.MAX_VALUE.toDouble())) {
                result.setAccuracy()
                result.setDecimal(Math.pow(value(),other.value()))
                result
            } else {
                result.setUpper(Math.pow(upper.toDouble(),other.value()).toLong())
                result.setLower(Math.pow(lower.toDouble(),other.value()).toLong())
                result
            }
        } else
            Num(Math.pow(value(),other.value()))
    }

}

