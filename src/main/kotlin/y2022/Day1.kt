package y2022

import common.Solver

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
        val list = input.partitionOnEmpty()
        return list.map { elf -> elf.ints().sum() }.max()
    }

    override fun solveb(input: List<String>): Any {
        return input.partitionOnEmpty().map { elf -> elf.ints().sum() }.sortedDescending().take(3).sum()
    }

}


fun main() {
    println(
        Day1.solve(
            """
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
    """.trimIndent()
        )
    )


    println(Day1.solve("day1.txt") == 68292) //68292
    println(Day1.solveb("day1.txt") == 203203) //203203
}