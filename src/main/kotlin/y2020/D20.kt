package y2020

import advent2020.Solver2020
import java.util.function.Predicate
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
        fun trimEdges() = Cube(id, body.take(body.size-1).drop(1).map { it.subList(1, it.size-1) })
        fun join(cube: Cube): Cube {
            check(cube.body.size == this.body.size)
            val newBody: List<List<Char>>  = this.body.mapIndexed { row, list -> list + cube.body[row] }
            return Cube(0L, newBody)
        }

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

        val availableCubes = mutableMapOf<Long, Cube>()
        cubes.values.forEach{c -> availableCubes[c.id] = c }

        picture[0][0] = corners.first()
        availableCubes.remove(corners.first().id)

        fun canMatchEdge(row: Int, col: Int): Boolean {
            if (row == 0 || col == 0 || row == dim - 1 || col == dim - 1) {
                return true
            }
            return picture[row - 1][col].id != 0L || picture[row + 1][col].id != 0L || picture[row][col - 1].id != 0L || picture[row][col + 1].id != 0L
        }

        while (availableCubes.isNotEmpty()) {
            val localMap = mutableMapOf<List<Char>, MutableSet<Long>>()
            val allCubesTransformed = mutableSetOf<Cube>()
            availableCubes.values.flatMap { cube -> allTransformations.map { tx -> tx.transform(cube) } + cube }.forEach { cube ->
                allCubesTransformed.add(cube)
                cube.sides().forEach { side -> localMap.computeIfAbsent(side) { HashSet() }.add(cube.id) }
            }

            fun northConstraint(row: Int, col: Int): Predicate<Cube> {
                return if(row == 0)  {
                    Predicate { c -> localMap[c.north()]!!.size == 1 }
                } else if(picture[row - 1][col].id != 0L) {
                    Predicate { c -> picture[row - 1][col].south() == c.north() }
                } else {
                    Predicate { c -> localMap[c.north()]!!.size > 1 }
                }
            }
            fun southContraint(row: Int, col: Int): Predicate<Cube> {
                return if(row == dim-1)  {
                    Predicate { c -> localMap[c.south()]!!.size == 1 }
                } else if(picture[row + 1][col].id != 0L) {
                    Predicate { c -> picture[row - 1][col].north() == c.south() }
                } else {
                    Predicate { c -> localMap[c.south()]!!.size > 1 }
                }
            }
            fun eastConstraint(row: Int, col: Int): Predicate<Cube> {
                return if(col == dim-1)  {
                    Predicate { c -> localMap[c.east()]!!.size == 1 }
                } else if(picture[row][col + 1].id != 0L) {
                    Predicate { c -> picture[row][col + 1].west() == c.east() }
                } else {
                    Predicate { c -> localMap[c.east()]!!.size > 1 }
                }
            }
            fun westConstraint(row: Int, col: Int): Predicate<Cube> {
                return if(col == 0)  {
                    Predicate { c -> localMap[c.west()]!!.size == 1 }
                } else if(picture[row][col - 1].id != 0L) {
                    Predicate { c -> picture[row][col - 1].east() == c.west() }
                } else {
                    Predicate { c -> localMap[c.west()]!!.size > 1 }
                }
            }

            fun candidates2(row: Int, col: Int): List<Cube> {
                val predicate = northConstraint(row, col).and(southContraint(row, col)).and(eastConstraint(row, col)).and ( westConstraint(row, col) )
                return allCubesTransformed.filter { predicate.test(it) }
            }

            (0 until dim).forEach { x ->
                (0 until dim).forEach { y ->
                    if (picture[x][y].id == 0L && canMatchEdge(x, y)) {
                        val candidates = candidates2(x, y)
                        if (candidates.size == 1) {
                            val cube = candidates.first()
                            availableCubes.remove(cube.id)
                            picture[x][y] = cube
                            println("inserting cube at $x $y with id ${cube.id} - remaining: ${availableCubes.size}")
                        }
                    }

                }
            }
        }

        picture.forEach { arr -> arr.forEach { cube -> check(cube.id != 0L) } }
        val tiled: Cube = buildPicture(picture)
        val patternString = arrayOf("                  # ",
                                    "#    ##    ##    ###",
                                    " #  #  #  #  #  #")
        val pattern = patternString.map { line ->
            line.withIndex().filter { it.value == '#' }.map { it.index }
        }
        val cubeList = allTransformations.map { it.transform(tiled) }
        return cubeList.map { cube -> val body = cube.body.map { it.joinToString  ("")  }
            countPatterns(body, pattern)
        }.max()
    }

    fun find(map: List<String>, pattern: List<List<Int>>, row: Int, col: Int): Boolean {
        if(map.size < row + 2) return false
        val first = pattern[0]
        val second = pattern[1]
        val third = pattern[2]
        if(first.all { index ->
                map[row].length > index+col && map[row][index+col] == '#'
            } &&
            second.all { index -> map[row+1].length > index+col && map[row+1][index+col] == '#' } &&
            third.all { index -> map[row+2].length > index+col && map[row+2][index+col] == '#' }) return true
        return false
    }

    private fun countPatterns(map: List<String>, pattern: List<List<Int>>): Int {
        val maxCol = pattern.maxOf { it.max() }
        return (0 until map.size-2).map { rowI ->
            val row = map[rowI]
            (row.indices).count { colI ->
                find(map, pattern, rowI, colI)
            }
        }.sum()
    }

    private fun buildPicture(picture: Array<Array<Cube>>): Cube {
        val trimmed: List<List<Cube>> = picture.map { row -> row.map { it.trimEdges() } }
        val newBody = trimmed.map { row ->
            row.reduce { acc, cube -> acc.join(cube) }
        }.map { it.body }.reduce{a, b -> a + b}
        newBody.forEach { println(it.joinToString("")) }
        return Cube(0L, newBody)
    }
}

fun main() {
//    D20.solveSample(20899048083289L)
    D20.solveb("day20.txt")
}