package y2022

import common.Solver

object D2 : Solver {

    val combinationsA = listOf(
        mapOf("Z" to "C", "Y" to "B", "X" to "A"),
//        mapOf("Z" to "A", "Y" to "C", "X" to "B"),
//        mapOf("Z" to "B", "Y" to "A", "X" to "C"),
//        mapOf("Z" to "B", "Y" to "C", "X" to "A"),
//        mapOf("Z" to "C", "Y" to "A", "X" to "B"),
//        mapOf("Z" to "C", "Y" to "B", "X" to "A")
    )

    //X for Rock, Y for Paper, and Z for Scissors. Winning every time would be
    val baseScores = mapOf("A" to 1, "B" to 2, "C" to 3)

    fun score(left: String, you: String, map: Map<String, String>): Int {
        val actualMine = map[you]!!
        val base = baseScores[actualMine]!!

        if(left == actualMine) {
            return 3 + base
        }
        return base + when(Pair(left, actualMine)) {
            Pair("A", "B")  -> 6
            Pair("B", "C")  -> 6
            Pair("C", "A")  -> 6
            else            -> 0
        }
    }

    override fun solve(input: List<String>): Any {
        val sorted: MutableMap<String, MutableMap<String, Int>> = HashMap()
        input.map { it.split(" ") }.forEach { entry ->
            val matchup = sorted.computeIfAbsent(entry[0]) { HashMap() }
            val count = matchup.getOrDefault(entry[1], 0)
            matchup[entry[1]] = count + 1
        }

        return combinationsA.map { scoreMap ->
            val bla = sorted.map { entry ->
                val left = entry.key
                entry.value.map { myShape ->
                    score(left, myShape.key, scoreMap) * myShape.value
                }.sum()
            }.sum()
            println("combination $scoreMap gave $bla")
            bla
        }.max()
    }

    fun scoreB(left: String, outcome: String): Int {
        if(outcome == "Y") return baseScores[left]!! + 3
        return when(Pair(left, outcome)) {
            Pair("A", "X") -> 3
            Pair("A", "Z") -> 8
            Pair("B", "X") -> 1
            Pair("B", "Z") -> 9
            Pair("C", "X") -> 2
            Pair("C", "Z") -> 7
            else -> TODO()
        }
    }

    //X means you need to lose, Y means you need to end the round in a draw, and Z means you need to win.
    override fun solveb(input: List<String>): Any {
        return input.map { it.split(" ") }.map { scoreB(it[0], it[1]) }.sum()
    }

}


fun main() {
    println(D2.solveb("""
        A Y
        B X
        C Z
    """.trimIndent()))

    /*
    A B 6 + 2
    B C 6 + 3
    C A 6 + 1
     */


    println(D2.solveb("day2.txt"))
}