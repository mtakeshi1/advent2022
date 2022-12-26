package y2022

import common.Direction
import common.Directions
import common.P2
import common.Solver
import java.util.*


object D23 : Solver {


//    override fun sample(): String = """
//       ..##.
//       ..#..
//       .....
//       ..##.
//    """.trimIndent()

    override fun sample(): String = """
        ....#..
        ..###.#
        #...#.#
        .#...##
        #.###..
        ##.#.##
        .#..#..
    """.trimIndent()

    //If there is no Elf in the N, NE, or NW adjacent positions, the Elf proposes moving north one step.
    //If there is no Elf in the S, SE, or SW adjacent positions, the Elf proposes moving south one step.
    //If there is no Elf in the W, NW, or SW adjacent positions, the Elf proposes moving west one step.
    //If there is no Elf in the E, NE, or SE adjacent positions, the Elf proposes moving east one step.
    //After each Elf has had a chance to propose a move, the second half of the round can begin.
    // Simultaneously, each Elf moves to their proposed destination tile if they were the only Elf to propose moving to that position.
    // If two or more Elves propose moving to the same position, none of those Elves move.
    // Finally, at the end of the round, the first direction the Elves considered is moved to the end of the list of directions.
    // For example, during the second round, the Elves would try proposing a move to the south first, then west, then east, then north.
    // On the third round, the Elves would first consider west, then east, then north, then south.

    data class Checker(val direction: Direction, val toCheck: List<Direction>)

    val allCheckers = arrayOf(
        Checker(Directions.north, listOf(Directions.north, Directions.ne, Directions.nw)),
        Checker(Directions.south, listOf(Directions.south, Directions.se, Directions.sw)),
        Checker(Directions.west, listOf(Directions.west, Directions.nw, Directions.sw)),
        Checker(Directions.east, listOf(Directions.east, Directions.ne, Directions.se))
    )


    data class Elf(var pos: P2) {

//        private val proposedMoves = LinkedList(allCheckers.toList())
        // the proposed move should cycle on the first

        fun reserve(existing: Set<Elf>, reservation: MutableMap<P2, Int>, proposedMoves: List<Checker>): P2 {
            if (Directions.allAdjacent.none { existing.contains(Elf(pos + it)) }) {
                return pos
            }
            val checker = proposedMoves.find { checker ->  checker.toCheck.map { pos + it }.none { existing.contains(Elf(it)) }}
//            proposedMoves.add(proposedMoves.poll())
            if(checker == null) {
                return pos
            }

//            var checker = proposedMoves.poll()
//            proposedMoves.add(checker)
//            while (!checker.toCheck.map { pos + it }.none { existing.contains(Elf(it)) }) {
//                checker = proposedMoves.next() // TODO here I'm eating the entire thing, but should only be eating the first
//            }

            val next = pos + checker.direction
            reservation.compute(next) { _, count -> count?.let { it + 1 } ?: 1 }
            return next
        }

        fun moveTo(reserved: P2): Elf {
            this.pos = reserved
            return this
        }
    }

    fun P2.reserve(existing: Set<P2>, reservation: MutableMap<P2, Int>, checker: Checker): P2 {
        val canReserve = checker.toCheck.map { this + it }.none { existing.contains(it) }
        if (canReserve) {
            val next = this + checker.direction
            reservation.compute(next) { _, count -> count?.let { it + 1 } ?: 1 }
            return next
        }
        return this
    }

    fun Set<Elf>.printElves() {
        val minX = this.minOf { it.pos.x }
        val maxX = this.maxOf { it.pos.x }
        val minY = this.minOf { it.pos.y }
        val maxY = this.maxOf { it.pos.y }
        (minY..maxY).forEach { y ->
            println((minX..maxX).joinToString("") { x -> if (this.contains(Elf(P2(x, y)))) "#" else "." })
        }
        println("---------------------------------------------")
    }

    override fun solve(input: List<String>): Any {
        val elves = input.withIndex().flatMap { iv ->
            val row = iv.index + 1
            iv.value.withIndex().filter { it.value == '#' }.map { Elf(P2(it.index + 1, row)) }
        }.toSet()
        val checkTurn = LinkedList(allCheckers.toList())
        val finalElves = (0 until 10).fold(elves) { localElves, _ ->
            val reservations = mutableMapOf<P2, Int>().withDefault { 0 }
            val result = elves.map { Pair(it, it.reserve(localElves, reservations, checkTurn.toList())) }.map { (original, reserved) ->
                if (original.pos != reserved && reservations.getOrDefault(reserved, 1) == 1) original.moveTo(reserved)
                else original
            }.toSet().apply { printElves() }
            checkTurn.add(checkTurn.poll())
            result
        }
        val minX = finalElves.minOf { it.pos.x }
        val maxX = finalElves.maxOf { it.pos.x }
        val minY = finalElves.minOf { it.pos.y }
        val maxY = finalElves.maxOf { it.pos.y }

        return (maxX - minX + 1) * (maxY - minY + 1) - finalElves.size
    }

    override fun solveb(input: List<String>): Any {
        val elves = input.withIndex().flatMap { iv ->
            val row = iv.index + 1
            iv.value.withIndex().filter { it.value == '#' }.map { Elf(P2(it.index + 1, row)) }
        }.toSet()

        tailrec fun calculateTurn(localElves: Set<Elf>, checkTurn: List<Checker>, turn: Int): Int {
            val reservations = mutableMapOf<P2, Int>().withDefault { 0 }
            val result = localElves.map { Pair(it, it.reserve(localElves, reservations, checkTurn.toList())) }.map { (original, reserved) ->
                if (original.pos != reserved && reservations.getOrDefault(reserved, 1) == 1) original.moveTo(reserved)
                else original
            }.toSet()
            return  if(localElves == result) turn
                    else calculateTurn(result, checkTurn.drop(1) + checkTurn.first(), turn + 1)
        }

        return calculateTurn(elves, allCheckers.toList(), 1)
    }
}

fun main() {
//    D23.solveSample(110)
//    D23.solve("day23.txt").println()

    D23.solveSampleB(20)
    D23.solveb("day23.txt").println()
}