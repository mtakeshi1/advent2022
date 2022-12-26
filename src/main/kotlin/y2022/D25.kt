package y2022

import common.Solver

object D25 : Solver {

    fun toDecimal(line: String, multi: Long = 1): Long {
        if(line.isEmpty()) return 0
        val rem = line.substring(0, line.length -1)
        val lastChar = line.last()
        val digit: Long = when(lastChar) {
            '=' -> -2L
            '-' -> -1L
            else -> (lastChar - '0').toLong()
        }
        return digit * multi +  toDecimal(rem, multi * 5)
    }

    // 2 digits max (base5) ->  44 ==  4 * 5 + 4 == 24
    // 2 digits min (base5) ->  00 ==  0
    // 2 digits max (base5*) -> 22 ==  2 * 5 + 2 == 12
    // 2 digits min (base5*) -> == == -2 * 5 + (-2) == -12
    // 1- -> 4
}

fun main() {
    println(D25.toDecimal("1=-0-2"))
}