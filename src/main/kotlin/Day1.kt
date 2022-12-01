package main.kotlin

object Day1 : Solver {

    fun sumAll(input: List<String>): List<Long> {
        return if (input.isEmpty()) listOf()
        else {
            val toSum = input.takeWhile { it.isNotBlank() }
            val next = input.drop(toSum.size + 1)
            sumAll(next) + listOf(toSum.sumOf { it.toLong() })
        }
    }

    override fun solve(input: List<String>): Any {
        return sumAll(input.map { it.trim() }).max()
    }

    override fun solveb(input: List<String>): Any {
        return sumAll(input.map { it.trim() }).sortedDescending().take(3).sum()

    }

}


fun main() {
    println(Day1.solve("""
        1000
        2000
        3000

        4000

        5000
        6000

        7000
        8000
        9000

        10000
    """.trimIndent()))


    println(Day1.solve("day1.txt"))
    println(Day1.solveb("day1.txt"))
}