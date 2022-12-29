package y2020

import advent2020.Solver2020

object D1 : Solver2020 {

    override fun solve(input: List<String>): Any {
        val all = input.asSequence().map { it.toInt() }
        val pairs = all.flatMap { x -> all.filter { it != x }.map { Pair(x, it) } }
        return pairs.find { it.first + it.second == 2020 }!!.let { it.first * it.second }
    }

    override fun solveb(input: List<String>): Any {
        val all = input.asSequence().map { it.toInt() }
        val pairs = all.flatMap { x -> all.filter { it != x }
            .flatMap { y -> all.filter { it != x && it != y }.map { Triple(x, y, it) } } }
        return pairs.find { it.first + it.second  + it.third == 2020 }!!.let { it.first * it.second * it.third }
    }

}

fun main() {

    D1.solve("day1a.txt")
    D1.solveb("day1a.txt")
}
