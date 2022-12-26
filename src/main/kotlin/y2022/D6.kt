package y2022

import common.Solver

object D6 : Solver {

    fun solveFor(input: String, numChars: Int): Int {
        return input.withIndex().windowed(numChars).find { wordWithIndex ->
            wordWithIndex.map { it.value }.toSet().count() == numChars
        }!!.first().index + numChars
    }

    override fun solve(input: List<String>): Any = solveFor(input.first(), 4)
    override fun solveb(input: List<String>): Any = solveFor(input.first(), 14)
}

fun main() {
    println(D6.solve("""bvwbjplbgvbhsrlpgdmjqwftvncz"""))
    println(D6.solve("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg"))
    println(D6.solve("day6.txt"))
    println(D6.solveb("day6.txt"))

    println(D6.solve("""bvwbjplbgvbhsrlpgdmjqwftvncz""") == 5)
    println(D6.solve("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg") == 10)
    println(D6.solve("day6.txt") == 1640)
    println(D6.solveb("day6.txt") == 3613)
}