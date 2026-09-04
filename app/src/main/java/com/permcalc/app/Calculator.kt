package com.permcalc.app

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

private const val MAX_DIGITS = 12

/** Port of the original useCalculator hook: standard 4-function calculator state. */
class CalculatorState {
    var display by mutableStateOf("0")
        private set
    var operator by mutableStateOf<Char?>(null)
        private set

    private var prevValue: Double? = null
    private var waitingForOperand = false

    fun inputDigit(d: Char) {
        if (waitingForOperand) {
            display = d.toString()
            waitingForOperand = false
        } else {
            display = when {
                display == "0" -> d.toString()
                display.length < MAX_DIGITS -> display + d
                else -> display
            }
        }
    }

    fun inputDecimal() {
        if (waitingForOperand) {
            display = "0."
            waitingForOperand = false
            return
        }
        if (!display.contains(".")) display += "."
    }

    fun clear() {
        display = "0"
        prevValue = null
        operator = null
        waitingForOperand = false
    }

    fun toggleSign() {
        val v = display.toDoubleOrNull() ?: return
        display = format(v * -1)
    }

    fun percentage() {
        val v = display.toDoubleOrNull() ?: return
        display = format(v / 100)
    }

    fun handleOperator(nextOp: Char) {
        val current = display.toDoubleOrNull() ?: 0.0
        if (operator != null && !waitingForOperand) {
            val result = compute(prevValue ?: 0.0, current, operator!!)
            display = format(result)
            prevValue = result
        } else {
            prevValue = current
        }
        waitingForOperand = true
        operator = nextOp
    }

    fun equals() {
        val op = operator ?: return
        if (waitingForOperand) return
        val current = display.toDoubleOrNull() ?: 0.0
        val result = compute(prevValue ?: 0.0, current, op)
        display = format(result)
        prevValue = null
        operator = null
        waitingForOperand = true
    }

    private fun compute(a: Double, b: Double, op: Char): Double = when (op) {
        '+' -> a + b
        '-' -> a - b
        '*' -> a * b
        '/' -> if (b != 0.0) a / b else 0.0
        else -> b
    }

    private fun format(value: Double): String {
        if (!value.isFinite()) return "0"
        // Integers render without a trailing ".0"
        val asStr = if (value == value.toLong().toDouble()) value.toLong().toString() else value.toString()
        if (asStr.length > MAX_DIGITS) {
            return value.toBigDecimal().round(java.math.MathContext(8)).stripTrailingZeros().toPlainString()
        }
        return asStr
    }
}
