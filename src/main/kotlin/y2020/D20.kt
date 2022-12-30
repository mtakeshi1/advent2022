package y2020

import advent2020.Solver2020
import kotlin.math.sqrt

object D20 : Solver2020 {
    // upside down

    override fun sample() = "day20-sample.txt"

    data class Cube(
        val id: Long,
        val body: List<List<Char>>
    ) {
        fun north(): List<Char> = body.first()
        fun east(): List<Char> = body.map { it.last() }
        fun south(): List<Char> = body.last()
        fun west(): List<Char> = body.map { it.first() }

        fun column(c: Int) = body.map { it[c] }

        fun sides() = listOf(north(), east(), west(), south())
    }

    fun interface CubeTransformation {
        fun transform(cube: Cube): Cube

        fun andThen(other: CubeTransformation) = CubeTransformation { c -> this.transform(other.transform(c)) }

    }


    val eastToWest = CubeTransformation {
        it.copy(
            body = it.body.map { it.reversed() }
//            north = it.north.reversed(),
//            south = it.south.reversed(),
//            east = it.west,
//            west = it.east
        )
    }
    val upsideDown = CubeTransformation {
        it.copy(
            body = it.body.reversed()
//            east = it.east.reversed(),
//            west = it.west.reversed(),
//            north = it.south,
//            south = it.north
        )
    }
    val clockWiseRotation = CubeTransformation { cube ->
        cube.copy(
            body = (0 until cube.body.size).map { c -> cube.column(c) }
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
        return Cube(id, cube.drop(1).map { it.toList() })
    }

    override fun solve(input: List<String>): Any {
        val cubes = input.partitionOnEmpty().map { parseCube(it) }
        val map = mutableMapOf<List<Char>, MutableSet<Long>>()
        val allCubeRotations = cubes.flatMap { cube -> allTransformations.map { tx -> tx.transform(cube) } + cube }
        allCubeRotations.forEach { cube ->
            cube.sides().forEach { side -> map.computeIfAbsent(side) { HashSet() }.add(cube.id) }
        }
        val topLeft = cubes.filter { cube ->
            val rotated = allTransformations.map { tx -> tx.transform(cube) } + cube
            rotated.any { c -> map[c.north()]!!.size == 1 && map[c.west()]!!.size == 1 }
        }.map { it.id }
        val topRight = cubes.filter { cube ->
            val rotated = allTransformations.map { tx -> tx.transform(cube) } + cube
            rotated.any { c -> map[c.north()]!!.size == 1 && map[c.east()]!!.size == 1 }
        }.map { it.id }
        val botRight = cubes.filter { cube ->
            val rotated = allTransformations.map { tx -> tx.transform(cube) } + cube
            rotated.any { c -> map[c.south()]!!.size == 1 && map[c.east()]!!.size == 1 }
        }.map { it.id }
        val botLeft = cubes.filter { cube ->
            val rotated = allTransformations.map { tx -> tx.transform(cube) } + cube
            rotated.any { c -> map[c.south()]!!.size == 1 && map[c.west()]!!.size == 1 }
        }.map { it.id }
        println(topLeft)
        println(topRight)
        println(botLeft)
        println(botRight)
//        map.mapValues { it.value.size }.forEach{println(it)}
        return topRight.reduce { a, b -> a * b }
    }

    override fun solveb(input: List<String>): Any {
        val cubes = input.partitionOnEmpty().map { parseCube(it) }.associateBy { it.id }
        val map = mutableMapOf<List<Char>, MutableSet<Long>>()
        val allCubeRotations =
            cubes.values.flatMap { cube -> allTransformations.map { tx -> tx.transform(cube) } + cube }
        allCubeRotations.forEach { cube ->
            cube.sides().forEach { side -> map.computeIfAbsent(side) { HashSet() }.add(cube.id) }
        }
        val corners = cubes.values.filter { cube ->
            val rotated = allTransformations.map { tx -> tx.transform(cube) } + cube
            rotated.any { c -> map[c.north()]!!.size == 1 && map[c.west()]!!.size == 1 }
        }
        check(corners.size == 4)
        val dim = sqrt(cubes.size.toDouble()).toInt()
        val picture = Array(dim) { Array(dim) { Cube(0, emptyList()) } }

        val availableCubes = mutableSetOf<Cube>()
        cubes.values.forEach(availableCubes::add)
        availableCubes.removeAll(corners)

        picture[0][0] = corners.first()
        fun fill( row: Int,  col: Int) {
            availableCubes.filter { cube ->
                val rotated = allTransformations.map { tx -> tx.transform(cube) } + cube
                rotated.any {
                        c -> map[c.north()]!!.size == 1 && c.west() == corners.first().east()
                }
            }
        }
        val nextCube = availableCubes.filter { cube ->
            val rotated = allTransformations.map { tx -> tx.transform(cube) } + cube
            rotated.any { c -> map[c.north()]!!.size == 1 && c.west() == corners.first().east() }
        }

        check(nextCube.size == 1)

        return 1
    }
}

fun main() {
//    D20.solveSample(20899048083289L)
    D20.solveb("day20.txt")
}