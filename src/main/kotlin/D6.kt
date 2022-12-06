package main.kotlin

object D6 : Solver {

    fun solveFor(input: String, numChars: Int): Int {
        return input.withIndex().drop(numChars-1).find { iv ->
            ((iv.index - numChars + 1)..iv.index).map { input[it] }.toSet().count() == numChars
        }!!.index + 1
    }

    override fun solve(input: List<String>): Any = solveFor(input.first(), 4)
    override fun solveb(input: List<String>): Any = solveFor(input.first(), 14)
}

fun main() {
    println(D6.solve("""bvwbjplbgvbhsrlpgdmjqwftvncz"""))
    println(D6.solve("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg"))
    println(D6.solve("day6.txt"))
    println(D6.solveb("day6.txt"))
}