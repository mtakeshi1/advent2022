package advent2020

import java.lang.RuntimeException

object Day11 : Solver2020 {

    enum class State(val c: Char) {
        FLOOR('.'), EMPTY('L'), OCCUPIED('#');

        companion object {
            fun parse(c: Char): State {
                return when (c) {
                    FLOOR.c -> FLOOR
                    EMPTY.c -> EMPTY
                    OCCUPIED.c -> OCCUPIED
                    else -> throw RuntimeException()
                }
            }

            fun parse(s: String): List<State> = s.toCharArray().map { parse(it) }
        }

        override fun toString(): String {
            return c.toString()
        }

    }

    fun adjacent(input: List<List<State>>, row: Int, col: Int): List<State> {
        fun valid(x: Int, y: Int): Boolean = x >= 0 && x < input.size && y >= 0 && y < input[0].size

        val indices = listOf(-1, 0, 1)
            .flatMap { x -> listOf(-1, 0, 1).map { Pair(x + row, it + col) } }
            .filter { it.first != row || it.second != col }
            .filter { valid(it.first, it.second) }
        return indices.map { input[it.first][it.second] }
    }

    fun nextState(input: List<List<State>>, row: Int, col: Int): State {
        val current = input[row][col]
        val neigh = adjacent(input, row, col)
        return when (current) {
            State.EMPTY -> if (neigh.none { it == State.OCCUPIED }) State.OCCUPIED else State.EMPTY
            State.OCCUPIED -> if (neigh.count { it == State.OCCUPIED } >= 4) State.EMPTY else State.OCCUPIED
            else -> current
        }
    }

    fun nextState(input: List<List<State>>): List<List<State>> {
        return (0 until input.size).map { row ->
            (0 until input[row].size).map { col ->
                nextState(input, row, col)
            }
        }
    }


    override fun solve(input: List<String>): Any {
        var state = input.map { State.parse(it) }
//        var state1 = nextState(state)
//        println(format(state1))
//        println("-----------------------------------------")
//        var state2 = nextState(state1)
//        println(format(state2))
//        println("-----------------------------------------")
        var next = nextState(state)
        while (next != state) {
//            println("-----------------------------------------")
//            println(format(next))
            state = next
            next = nextState(next)
        }

        println(format(next))

        return next.flatMap { it.filter { it == State.OCCUPIED } }.count()

    }

    fun format(input: List<List<State>>): String {
        return input.map { it.joinToString("") }.joinToString("\n")
    }

}

fun main() {
    println(
        Day11.solve(
            """
        L.LL.LL.LL
        LLLLLLL.LL
        L.L.L..L..
        LLLL.LL.LL
        L.LL.LL.LL
        L.LLLLL.LL
        ..L.L.....
        LLLLLLLLLL
        L.LLLLLL.L
        L.LLLLL.LL
    """.trimIndent()
        )
    )

    println(Day11.solve("day11.txt"))
}