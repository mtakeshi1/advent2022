package y2022

import common.Solver

object D25 : Solver {

    override fun sample(): String = """
        1=-0-2
        12111
        2=0=
        21
        2=01
        111
        20012
        112
        1=-1=
        1-12
        12
        1=
        122
    """.trimIndent()

    fun toDecimal(line: String, multi: Long = 1): Long {
        if (line.isEmpty()) return 0
        val rem = line.substring(0, line.length - 1)
        val lastChar = line.last()
        val digit: Long = when (lastChar) {
            '=' -> -2L
            '-' -> -1L
            else -> (lastChar - '0').toLong()
        }
        return digit * multi + toDecimal(rem, multi * 5)
    }

    fun maxDigits(num: Long, digits: Int = 1): Int {
        val x = Math.pow(5.toDouble(), digits.toDouble())
        if (x < num) {
            return maxDigits(num, digits + 1)
        }
        return digits
    }

    val digits = arrayOf('=', '-', '0', '1', '2')

    fun binarySearch(target: Long, underConstruction: CharArray): String {

        var i = 0
        while (i < underConstruction.size) {
            val factor = Math.pow(5.0, underConstruction.size.toDouble() - i - 1.0).toLong() - 1
            val nChar = digits.withIndex().find {
                underConstruction[i] = it.value
                val min = toDecimal(String(underConstruction, 0, underConstruction.size))
                val max = min + factor
                target in min..max
            }
            if(nChar != null && nChar.index > 0) {
                underConstruction[i] = nChar.value
            }
            val n = toDecimal(String(underConstruction, 0, underConstruction.size))
            if(n == target) return String(underConstruction, 0, underConstruction.size)
            i++
        }
        return String(underConstruction, 0, underConstruction.size)
    }


    // 2 digits max (base5) ->  44 ==  4 * 5 + 4 == 24
    // 2 digits min (base5) ->  00 ==  0
    // 2 digits max (base5*) -> 22 ==  2 * 5 + 2 == 12
    // 2 digits min (base5*) -> == == -2 * 5 + (-2) == -12
    // 1- -> 4

    override fun solve(input: List<String>): Any {
        val target = input.map { toDecimal(it) }.sum()
        val digits = maxDigits(target)
        val underConstruction = CharArray(digits).apply { fill('=') }
        return binarySearch(target, underConstruction)
//        return target
    }

}

fun main() {
    D25.solveSample("2=-1=0")
    D25.solve("day25.txt")
}