package main.kotlin

import kotlin.math.abs

object D9 : Solver {

    fun Pair<Int, Int>.move(direction: String): Pair<Int, Int> {
        return when (direction) {
            "R" -> this.copy(first = this.first + 1)
            "L" -> this.copy(first = this.first - 1)
            "U" -> this.copy(second = this.second + 1)
            "D" -> this.copy(second = this.second - 1)
            else -> TODO()
        }
    }

    fun Pair<Int, Int>.touching(other: Pair<Int, Int>) =
        abs(this.first - other.first) <= 1 && abs(this.second - other.second) <= 1

    fun compareInts(left: Int, right: Int): Int = if (left == right) 0 else if (left < right) -1 else 1

    private fun adjust(head: Pair<Int, Int>, tail: Pair<Int, Int>): Pair<Int, Int> {
        if (head.touching(tail)) {
            return tail;
        }
        val xDiff = compareInts(head.first, tail.first)
        val yDiff = compareInts(head.second, tail.second)
        return Pair(tail.first + xDiff, tail.second + yDiff)
    }

    fun updatePositions(
        headTail: Pair<Pair<Int, Int>, Pair<Int, Int>>,
        move: Pair<String, Int>,
        positionsTail: MutableSet<Pair<Int, Int>>
    ): Pair<Pair<Int, Int>, Pair<Int, Int>> {
        var (head, tail) = headTail
        positionsTail.add(tail)
        for (i in (0 until move.second)) {
            head = head.move(move.first)
            while (!tail.touching(head)) {
                tail = adjust(head, tail)
                positionsTail.add(tail)
            }
        }
        return Pair(head, tail)
    }

    override fun solve(input: List<String>): Any {
        val moves = input.map { line -> line.split(" ").let { Pair(it[0], it[1].toInt()) } }
        val positionsTail: MutableSet<Pair<Int, Int>> = HashSet()
        moves.fold(Pair(Pair(0, 0), Pair(0, 0))) { pos, move ->
            updatePositions(pos, move, positionsTail)
        }
        return positionsTail.size
    }

    fun adjustBodyRecursively(
        previous: Pair<Int, Int>,
        snake: List<Pair<Int, Int>>,
        tailPositions: MutableSet<Pair<Int, Int>>
    ): List<Pair<Int, Int>> {
        if (snake.isEmpty()) return listOf()
        var toMove = snake.first()
        while (!previous.touching(toMove)) {
            if (snake.size == 1) {
                tailPositions.add(toMove)
            }
            toMove = adjust(previous, toMove)
            if (snake.size == 1) {
                tailPositions.add(toMove)
            }
        }
        return listOf(toMove) + adjustBodyRecursively(toMove, snake.drop(1), tailPositions)
    }

    fun moveSnake(snake: List<Pair<Int, Int>>, move: Pair<String, Int>, tailPositions: MutableSet<Pair<Int, Int>>): List<Pair<Int, Int>> {
        return (0 until move.second).fold(snake) { newSnake, _ ->
            val head = newSnake.first().move(move.first)
            val rest = adjustBodyRecursively(head, newSnake.drop(1), tailPositions)
            listOf(head) + rest
        }
    }

    override fun solveb(input: List<String>): Any {
        val moves = input.map { line -> line.split(" ").let { Pair(it[0], it[1].toInt()) } }
        val body = (0 until 10).map { Pair(0, 0) }
        val tailPositions: MutableSet<Pair<Int, Int>> = HashSet()
        moves.fold(body) { snake, move ->
            moveSnake(snake, move, tailPositions)
        }
        return tailPositions.size
    }
}

fun main() {
    println(
        D9.solve(
            """
        R 4
        U 4
        L 3
        D 1
        R 4
        D 1
        L 5
        R 2
    """.trimIndent()
        )
    )

    println(D9.solve("day9.txt"))

    println(
        D9.solveb(
            """
        R 5
        U 8
        L 8
        D 3
        R 17
        D 10
        L 25
        U 20
    """.trimIndent()
        )
    )

    println(D9.solveb("day9.txt"))
}