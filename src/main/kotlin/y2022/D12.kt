package y2022

import common.Solver
import java.util.*
import kotlin.collections.HashMap

object D12 : Solver {

    val dirs = listOf(Pair(1, 0), Pair(-1, 0), Pair(0, 1), Pair(0, -1))

    val dirNames = listOf("S", "N", "E", "W")
    val dirSymbols = listOf("V", "^", ">", "<")

    fun findSymbol(from: Pair<Int, Int>, to: Pair<Int, Int>): String = (0 until 4).find { from + dirs[it] == to }.let { dirSymbols[it!!] }


    data class PositionWithPredecessor(val pre: PositionWithPredecessor?, val pos: Pair<Int, Int>, val len: Int)

    fun path(map: List<List<Char>>, startingPosition: Pair<Int, Int>, goal: Pair<Int, Int>, cache: MutableMap<Pair<Int, Int>, Int> = HashMap()): Int {
        val cmp: Comparator<PositionWithPredecessor> = Comparator.comparing{a -> a.len * 10000 + a.pos.distanceFrom(goal)}
        val fringe: PriorityQueue<PositionWithPredecessor> = PriorityQueue(cmp)
        val visited: MutableSet<Pair<Int, Int>> = HashSet()
        fringe.add(PositionWithPredecessor(null, startingPosition, 0))
        while (fringe.isNotEmpty()) {
            val withPredecessor = fringe.remove()
            val pos = withPredecessor.pos
            if(visited.contains(pos)) continue
            val cached = cache[pos]
            if(cached != null) {
                return cached;
            }
            visited.add(pos)
            if (map[pos.first][pos.second] == 'E') {
                cache[pos] = withPredecessor.len
                var pre = withPredecessor.pre
                while (pre != null) {
                    cache[pre.pos] = withPredecessor.len - pre.len
                    pre = pre.pre
                }
                return withPredecessor.len
            }
            neighboorsOf(pos, map).filter { !visited.contains(it) }.map { PositionWithPredecessor(withPredecessor, it, withPredecessor.len + 1) }
                .forEach { fringe.add(it) }
        }
        return Integer.MAX_VALUE
    }

    private fun neighboorsOf(pos: Pair<Int, Int>, map: List<List<Char>>): List<Pair<Int, Int>> {
        fun symbol(r: Int, c: Int) = map[r][c].let { if (it == 'S') 'a' else if (it == 'E') 'z' else it }
        val current = symbol(pos.first, pos.second)
        val filter = dirs.map { pos + it }
            .filter {
                it.first >= 0 && it.second >= 0
                        && it.first < map.size
                        && it.second < map[it.first].size
                        && symbol(it.first, it.second) <= (current + 1)
            }
        return filter
    }

    private fun collect(withPredecessor: PositionWithPredecessor, acc: List<Pair<Int, Int>> = emptyList()): List<Pair<Int, Int>> {
        return if (withPredecessor.pre == null) acc + withPredecessor.pos
        else collect(withPredecessor.pre, acc + withPredecessor.pos)
    }


    override fun solve(input: List<String>): Any {
        val map = input.map { it.toList().toMutableList() }

        val starting = map.withIndex().flatMap { row ->
            row.value.withIndex().filter { col -> col.value == 'S' }.map { Pair(row.index, it.index) }
        }.first()
        val end = map.withIndex().flatMap { row ->
            row.value.withIndex().filter { col -> col.value == 'E' }.map { Pair(row.index, it.index) }
        }.first()
        return path(map, starting, end)
    }
    override fun solveb(input: List<String>): Any {
        val map = input.map { it.toList().toMutableList() }
        val startingPoints = map.withIndex().flatMap { row -> row.value.withIndex().filter { col -> col.value == 'a' }.map { Pair(row.index, it.index) } }
        val cache: MutableMap<Pair<Int, Int>, Int> = HashMap()
        val end = map.withIndex().flatMap { row -> row.value.withIndex().filter { col -> col.value == 'E' }.map { Pair(row.index, it.index) } }.first()
        return startingPoints.map {
            path(map, it, end, cache)
        }.min()
    }

}

fun main() {
    //v..v<<<<
    //>v.vv<<^
    //.>vv>E^^
    //..v>>>^^
    //..>>>>>^
//    println(D12.solve("""
//        Sabqponm
//        abcryxxl
//        accszExk
//        acctuvwj
//        abdefghi
//    """.trimIndent()))
    println(D12.solveb("day12.txt"))
}