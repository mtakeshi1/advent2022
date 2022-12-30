package y2020

import advent2020.Solver2020
import y2022.println
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.zip.GZIPOutputStream

object D20 : Solver2020 {
    // upside down

    override fun sample() = "day20-sample.txt"

    data class Cube(
        val id: Long,
        val north: List<Char>,
        val east: List<Char>,
        val south: List<Char>,
        val west: List<Char>
    ) {
        fun sides() = listOf(north, east, west, south)
    }

    fun interface CubeTransformation {
        fun transform(cube: Cube): Cube

        fun andThen(other: CubeTransformation) = CubeTransformation { c -> this.transform(other.transform(c)) }

    }


    val eastToWest = CubeTransformation {
        it.copy(
            north = it.north.reversed(),
            south = it.south.reversed(),
            east = it.west,
            west = it.east
        )
    }
    val upsideDown = CubeTransformation {
        it.copy(
            east = it.east.reversed(),
            west = it.west.reversed(),
            north = it.south,
            south = it.north
        )
    }
    val clockWiseRotation = CubeTransformation {
        it.copy(
            north = it.west.reversed(),
            east = it.north,
            south = it.east,
            west = it.south.reversed()
        )
    }

    val allTurns = listOf(
        clockWiseRotation, clockWiseRotation.andThen(clockWiseRotation), clockWiseRotation.andThen(
            clockWiseRotation
        ).andThen(clockWiseRotation)
    )

    val allTransformations = allTurns.flatMap {
        listOf(it, it.andThen(eastToWest), it.andThen(upsideDown), it.andThen(eastToWest).andThen(upsideDown))
    }

    private fun parseCube(cube: List<String>): Cube {
        val id = cube.first().anyInt()!!.toLong()
        val north = cube[1].toList()
        val south = cube.last().toList()
        val east = cube.drop(1).map { it.last() }
        val west = cube.drop(1).map { it.first() }
        return Cube(id, north, east, south, west)
    }

    fun compress(file: File): Double {
        val bout = ByteArrayOutputStream()
        val gout = GZIPOutputStream(bout)
        file.inputStream().copyTo(gout)
        gout.close()
        return bout.toByteArray().size.toDouble() / file.length()
    }

    override fun solve(input: List<String>): Any {
        val cubes = input.partitionOnEmpty().map { parseCube(it) }
        val map = mutableMapOf<List<Char>, MutableSet<Long>>()
        val allCubeRotations = cubes.flatMap { cube -> allTransformations.map { tx -> tx.transform(cube) } + cube }
        allCubeRotations.forEach { cube ->
            cube.sides().forEach { side -> map.computeIfAbsent(side) { HashSet() }.add(cube.id) }
        }
        val topLeft= cubes.filter { cube ->
            val rotated = allTransformations.map { tx -> tx.transform(cube) } + cube
            rotated.any{ c -> map[c.north]!!.size == 1 && map[c.west]!!.size == 1}
        }.map { it.id }
        val topRight = cubes.filter { cube ->
            val rotated = allTransformations.map { tx -> tx.transform(cube) } + cube
            rotated.any{ c -> map[c.north]!!.size == 1 && map[c.east]!!.size == 1}
        }.map{it.id}
        val botRight = cubes.filter { cube ->
            val rotated = allTransformations.map { tx -> tx.transform(cube) } + cube
            rotated.any{ c -> map[c.south]!!.size == 1 && map[c.east]!!.size == 1}
        }.map{it.id}
        val botLeft = cubes.filter { cube ->
            val rotated = allTransformations.map { tx -> tx.transform(cube) } + cube
            rotated.any{ c -> map[c.south]!!.size == 1 && map[c.west]!!.size == 1}
        }.map{it.id}
        println(topLeft)
        println(topRight)
        println(botLeft)
        println(botRight)
//        map.mapValues { it.value.size }.forEach{println(it)}
        return topRight.reduce{a, b-> a * b}
    }

}

fun main() {
//    D20.solveSample(20899048083289L)
    D20.solve("day20.txt")
}