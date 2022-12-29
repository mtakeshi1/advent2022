package y2022

import common.Solver2022
import java.util.LinkedList

object D18 : Solver2022 {

    data class Cube(val x: Int, val y: Int, val z: Int) {
        var faceCount = 6

        fun neighbors() = listOf(
            this.copy(x = x - 1), this.copy(x = x + 1),
            this.copy(y = y - 1), this.copy(y = y + 1),
            this.copy(z = z - 1), this.copy(z = z + 1)
        )
    }

    override fun solve(input: List<String>): Any {
        val rawCubes = input.ints3().map { Cube(it.first, it.second, it.third) }.toSet()
        val maxFaces = 6 * rawCubes.size
        val toSubtract = rawCubes.flatMap { c ->
            c.neighbors().filter { rawCubes.contains(it) }.map { 1 }
        }.sum()
        return maxFaces - toSubtract
    }



    fun findConnecting(from: Cube, cubes: Collection<Cube>, xRange: IntRange, yRange: IntRange, zRange: IntRange ): Set<Cube>? {
        val found = mutableSetOf<Cube>()
        val queue = LinkedList<Cube>()
        queue.add(from)
        fun inbound(cube: Cube) = cube.x in xRange && cube.y in yRange && cube.z in zRange
        fun oob(cube: Cube) = !inbound(cube)

        while (queue.isNotEmpty()) {
            val next = queue.poll()
            if(!found.add(next)) continue
            if(next.neighbors().find { oob(it) } != null) return null
            next.neighbors().filter { cubes.contains(it) }.filter { inbound(it) }.filterNot { found.contains(it) }.forEach { queue.add(it) }
        }
        return found
    }

    override fun solveb(input: List<String>): Any {
        val rawCubes = input.ints3().map { Cube(it.first, it.second, it.third) }.toSet()
        val minX = rawCubes.minOf { it.x }
        val maxX = rawCubes.maxOf { it.x }
        val minY = rawCubes.minOf { it.y }
        val maxY = rawCubes.maxOf { it.y }
        val minZ = rawCubes.minOf { it.z }
        val maxZ = rawCubes.maxOf { it.z }

        val xRange = minX..maxX
        val yRange = minY..maxY
        val zRange = minZ..maxZ
        val dual = xRange.flatMap { x -> yRange.flatMap { y -> zRange.map { z -> Cube(x, y, z) } } }.filter { !rawCubes.contains(it) }

        val inner = mutableSetOf<Cube>()

        val innerCubes = mutableListOf<Set<Cube>>()

        dual.forEach { cube ->
            if(!inner.contains(cube)) {
                findConnecting(cube, dual, xRange, yRange, zRange)?.let { set ->
                    set.forEach { inner.add(it) }
                    innerCubes.add(set)
                }
            }
        }


        return facesFrom(rawCubes) - innerCubes.map { facesFrom(it) }.sum()
    }

    private fun facesFrom(rawCubes: Set<Cube>): Int {
        val maxFaces = 6 * rawCubes.size
        val toSubtract = rawCubes.flatMap { c ->
            c.neighbors().filter { rawCubes.contains(it) }.map { 1 }
        }.sum()
        return maxFaces - toSubtract
    }

    override fun sample(): String = """
        2,2,2
1,2,2
3,2,2
2,1,2
2,3,2
2,2,1
2,2,3
2,2,4
2,2,6
1,2,5
3,2,5
2,1,5
2,3,5
    """.trimIndent()

}

fun main() {
//    D18.solveSample(64)
//    D18.solve("day18.txt")

    D18.solveSampleB(58)
    D18.solveb("day18.txt")
}