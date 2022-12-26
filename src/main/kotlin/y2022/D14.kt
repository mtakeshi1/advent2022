package y2022

import common.Solver
import kotlin.math.max
import kotlin.math.min


object D14 : Solver {


    val dropDirections = listOf(Pair(0, 1), Pair(-1, 1), Pair(1, 1))

    fun parseSegments(input: String): List<Pair<Int, Int>> =
        input.split(" -> ").map { it.split(",").let { Pair(it[0].toInt(), it[1].toInt()) } }

    enum class Cell(val symbol: String) {
        Abyss(" "), Empty("."), Wall("#"), Sand("o");

        override fun toString(): String = symbol
    }


    fun simulateOne(map: Array<Array<Cell>>): Boolean {
        var sand = Pair(500, 0)
        while (sand.first < map.size && sand.second < map[0].size - 1) {
            sand =
                dropDirections.map { it + sand }.firstOrNull { map[it.first][it.second] == Cell.Empty } ?: break
        }
        if (sand.second == map[0].size - 1) return true
        map[sand.first][sand.second] = Cell.Sand
        return false
    }

    override fun solve(input: List<String>): Any {
        val segments = input.flatMap { line ->
            val segs = parseSegments(line)
            segs.zip(segs.drop(1))
        }
        val maxX = segments.map { max(it.first.first, it.second.first) }.max() + 1
        val maxY = segments.map { max(it.first.second, it.second.second) }.max() + 2
        val map: Array<Array<Cell>> = Array(maxX) { Array(maxY) { Cell.Empty } }
        segments.forEach { segment ->
            val lx = min(segment.first.first, segment.second.first)
            val mx = max(segment.first.first, segment.second.first)

            val ly = min(segment.first.second, segment.second.second)
            val my = max(segment.first.second, segment.second.second)

            for (x in lx..mx) {
                for (y in ly..my) {
                    map[x][y] = Cell.Wall
                }
            }
        }
        var i = 0;
        while (!simulateOne(map)) {
            i++
        }
        return i
    }

    fun simulateOneb(map: Array<Array<Cell>>) {
        var sand = Pair(500, 0)
        while (sand.first < map.size && sand.second < map[0].size) {
            sand =
                dropDirections.map { it + sand }.firstOrNull { map[it.first][it.second] == Cell.Empty } ?: break
            if (sand.first < 0) {
                TODO()
            }
        }
        if (sand.second == map[0].size) TODO()
        map[sand.first][sand.second] = Cell.Sand
        return
    }

    fun printMap(map: Array<Array<Cell>>) {
        var minX = map.size
        var minY = map.size
        var maxX = 0
        var maxY = 0

        for (x in map.indices) {
            for (y in 0 until map[x].size - 1) {
                if (map[x][y] != Cell.Empty) {
                    minX = min(x, minX)
                    minY = min(y, minY)
                    maxX = max(x, maxX)
                    maxY = max(y, maxY)
                }
            }
        }
        for(y in minY..maxY) {
            for(x in minX..maxX) {
                print(map[x][y])
            }
            println()
        }
        for(x in minX..maxX) {
            print(map[x][maxY+1])
        }
        println()
    }

    override fun solveb(input: List<String>): Any {
        val segments = input.flatMap { line ->
            val segs = parseSegments(line)
            segs.zip(segs.drop(1))
        }
        val maxX = 1000
        val maxY = segments.map { max(it.first.second, it.second.second) }.max() + 3
        val map: Array<Array<Cell>> = Array(maxX) { Array(maxY) { Cell.Empty } }
        segments.forEach { segment ->
            val lx = min(segment.first.first, segment.second.first)
            val mx = max(segment.first.first, segment.second.first)

            val ly = min(segment.first.second, segment.second.second)
            val my = max(segment.first.second, segment.second.second)

            for (x in lx..mx) {
                for (y in ly..my) {
                    map[x][y] = Cell.Wall
                }
            }
        }
        for (i in 0 until maxX) {
            map[i][maxY - 1] = Cell.Wall
        }
        var i = 0;
        printMap(map)
        while (map[500][0] != Cell.Sand) {
            simulateOneb(map)
            if((i % 5) == 0) {
//                println("after $i turns")
//                printMap(map)
            }
            i++
        }
        printMap(map)
        return i
    }

}

fun main() {
    println(
        D14.solveb(
            """
        498,4 -> 498,6 -> 496,6
        503,4 -> 502,4 -> 502,9 -> 494,9
    """.trimIndent()
        )
    )

    println(D14.solveb("day14.txt"))

}