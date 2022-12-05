package main.kotlin

import java.util.*

object D5 : Solver {

    data class Move(val num: Int, val from: Int, val to: Int) {
        fun apply(stacks: List<Deque<String>>): List<Deque<String>> {
            for (i in 0 until num) {
                val top = stacks[from].pop()
                stacks[to].push(top)
            }
            return stacks
        }

        fun applyB(stacks: List<Deque<String>>): List<Deque<String>> {
            val top = (0 until num).map { stacks[from].pop() }
            top.reversed().forEach { stacks[to].addFirst(it) }
            return stacks
        }
    }

    fun parseOne(line: String) = (line.indices).step(4).map { line.substring(it, it + 3).trim().replace("[", "").replace("]", "") }

    override fun solve(input: List<String>): Any {
        val stacks: List<Deque<String>> = parseStacks(input)
        val moves = parseMoves(input)
        val result = moves.fold(stacks) { s, move -> move.apply(s) }
        return result.joinToString(separator = "") { it.peekFirst() }
    }

    fun parseStacks(input: List<String>): List<Deque<String>> {
        val init = input.takeWhile { it.isNotEmpty() }.dropLast(1).map { parseOne(it) }
        val stacks: List<Deque<String>> = init[0].map { LinkedList() }
        for (i in init[0].indices) {
            init.forEach {
                if (it[i].isNotEmpty()) stacks[i].add(it[i])
            }
        }
        stacks.forEach { println(it) }
        return stacks
    }

    fun parseMoves(input: List<String>): List<Move> {
        return input.dropWhile { it.isNotEmpty() }.drop(1).map { Regex("move ([0-9]+) from ([0-9]+) to ([0-9]+)").matchEntire(it)!! }
            .map { Move(it.groupValues[1].toInt(), it.groupValues[2].toInt() - 1, it.groupValues[3].toInt() - 1) }
    }

    override fun solveb(input: List<String>): Any {
        val stacks: List<Deque<String>> = parseStacks(input)
        val moves = parseMoves(input)
        val result = moves.fold(stacks) { s, move -> move.applyB(s) }
        return result.joinToString(separator = "") { it.peekFirst() }
    }
}

fun main() {
    println(
        D5.solve(
            """
        [D]    
    [N] [C]    
    [Z] [M] [P]
    1   2   3 

    move 1 from 2 to 1
    move 3 from 1 to 3
    move 2 from 2 to 1
    move 1 from 1 to 2
    """.trimIndent()
        )
    )


    println(D5.solve("day5.txt"))
    println(D5.solveb("day5.txt"))
}