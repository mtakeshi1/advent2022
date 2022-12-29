package y2022

import common.Solver2022
import java.util.*

object D5 : Solver2022 {

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

    override fun solve(input: List<String>): Any {
        val (ss, ms) = input.splitOnEmpty()
        val sss = ss.dropLast(1).map { it.chunked(4).map { chunk -> chunk[1] } }.transposed().map { it.filter { c -> c != ' ' }.map { it.toString() } }
        val stacks: List<Deque<String>> = sss.map { LinkedList(it) }
        val moves = ms.ints3().map { (n, f, t) -> Move(n, f-1, t-1) }
        val result = moves.fold(stacks) { s, move -> move.apply(s) }
        return result.map{ it.peekFirst() }.join()
    }

    override fun solveb(input: List<String>): Any {
        val (ss, ms) = input.splitOnEmpty()
        val sss = ss.dropLast(1).map { it.chunked(4).map { chunk -> chunk[1] } }.transposed().map { it.filter { c -> c != ' ' }.map { it.toString() } }
        val stacks: List<Deque<String>> = sss.map { LinkedList(it) }
        val moves = ms.ints3().map { (n, f, t) -> Move(n, f-1, t-1) }
        val result = moves.fold(stacks) { s, move -> move.applyB(s) }
        return result.map{ it.peekFirst() }.join()
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


    println(D5.solve("day5.txt") == "FRDSQRRCD")
    println(D5.solveb("day5.txt") == "HRFTQVWNN")
}