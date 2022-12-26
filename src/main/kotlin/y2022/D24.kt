package y2022

import common.*
import kotlin.math.abs

object D24 : Solver {

    // TODO check < and ^

    override fun sample(): String = """
        #.######
        #>>.<^<#
        #.<..<<#
        #>v.><>#
        #<^v^^>#
        ######.#
    """.trimIndent()

    data class State(val x: Int, val y: Int, val turn: Int) {
        operator fun plus(direction: Direction) = State(this.x + direction.x, this.y + direction.y, turn + 1)
        fun waitInPlace() = this.copy(turn = turn  + 1)
        fun neighbours(map: Map<P2, Node>): List<State> = (Directions.cross.map { this + it } + waitInPlace()).filter { it.isSafe(map) }
        fun isSafe(map: Map<P2, Node>): Boolean {
            return map[P2(x, y)]?.canPass(this.turn) ?: false
        }

        fun manhatanDistance(other: P2) = abs(this.x - other.x) + abs(this.y - other.y)

    }

    interface Node {
        fun canPass(turn: Int): Boolean
    }
    object Wall : Node {
        override fun canPass(turn: Int): Boolean = false
    }
    data class Plain(val x: Int, val y: Int): Node {
        override fun canPass(turn: Int): Boolean = true
    }

    data class BlizzardOccurence(val frequency: Int, val waveTurnOffset: Int, val direction: Char) {
        fun occurs(turn: Int) = ((waveTurnOffset-turn).mod(frequency)) == 0
    }

    data class Blizzards(val x: Int, val y: Int): Node {
        var occurences = mutableListOf<BlizzardOccurence>()
        override fun canPass(turn: Int): Boolean  {
            return occurences.none { it.occurs(turn) }
        }
    }

    fun charForDir(direction: Direction) = when(direction) {
        Directions.east -> '>'
        Directions.west -> '<'
        Directions.south -> 'v'
        Directions.north -> '^'
        else -> '?'
    }

    override fun solve(input: List<String>): Any {
        val blizzards = mutableMapOf<P2, Direction>()
        val graph:MutableMap<P2, Node> = input.withIndex().flatMap { row ->
            val y = row.index + 1
            row.value.withIndex().map { col ->
                val x = col.index + 1
                val type: Node = when(col.value) {
                    '#' -> Wall
                    '.' -> Plain(x, y)
                    '>' -> {blizzards[P2(x, y)]= Directions.east ; Plain(x, y)}
                    '<' -> {blizzards[P2(x, y)]= Directions.west ; Plain(x, y)}
                    '^' -> {blizzards[P2(x, y)]= Directions.north ; Plain(x, y)}
                    'v' -> {blizzards[P2(x, y)]= Directions.south ; Plain(x, y)}
                    else -> TODO()
                }
                P2(x, y) to type
            }
        }.toMap().toMutableMap()
        val xRanges: Map<Int, IntRange> = graph.entries.groupBy { it.key.y }
            .mapValues { it.value.filter { it.value !is Wall }.let { it.minOf { it.key.x } .. it.maxOf { it.key.x } } }
        val yRanges: Map<Int, IntRange> = graph.entries.groupBy { it.key.x }.mapValues {
            val list: List<MutableMap.MutableEntry<P2, Node>> = it.value.filter { node -> node.value !is Wall }
            if (list.isEmpty()) IntRange(0, 0)
            else list.minOf { it.key.y } .. list.maxOf { it.key.y }
        }.filterValues { it.last > 0 }
        for ((pos, dir) in blizzards) {
            when (dir){
                Directions.east, Directions.west -> {
                    val intRange = xRanges[pos.y]!!
                    val len = intRange.last - intRange.first + 1
                    for(x in intRange) {
                        val occ = BlizzardOccurence(len, dir.x * (x - pos.x), charForDir(dir))
                        ((graph.merge(P2(x, pos.y), Blizzards(x, pos.y)) {old, new -> if(old is Blizzards) old else new }) as Blizzards).occurences.add(occ)
                    }
                }
                Directions.north, Directions.south -> {
                    val intRange = yRanges[pos.x]!!
                    val len = intRange.last - intRange.first + 1
                    for(y in intRange) {
                        val occ = BlizzardOccurence(len, (y - pos.y) * dir.y, charForDir(dir))
                        ((graph.merge(P2(pos.x, y), Blizzards(pos.x, y)) {old, new -> if(old is Blizzards) old else new }) as Blizzards).occurences.add(occ)
                    }
                }
            }
        }

        val startingPointY = 1
        val startingPointX = input.first().indexOf(".") + 1

        val finishPointY = input.size
        val finishPointX = input.last().indexOf(".") + 1

        val destination = P2(finishPointX, finishPointY)


        val theGraph = Graph.fromIdentity<State> { state ->
            state.neighbours(graph).asSequence()
        }
        val r = theGraph.aStar(State(startingPointX, startingPointY, 0), destination = { pos -> pos.x == finishPointX && pos.y == finishPointY} , filter = {_-> true} , { node -> node.turn * 1000000 + node.manhatanDistance(destination)})
        return r!!.last().turn
    }

    override fun solveb(input: List<String>): Any {
        val blizzards = mutableMapOf<P2, Direction>()
        val graph:MutableMap<P2, Node> = input.withIndex().flatMap { row ->
            val y = row.index + 1
            row.value.withIndex().map { col ->
                val x = col.index + 1
                val type: Node = when(col.value) {
                    '#' -> Wall
                    '.' -> Plain(x, y)
                    '>' -> {blizzards[P2(x, y)]= Directions.east ; Plain(x, y)}
                    '<' -> {blizzards[P2(x, y)]= Directions.west ; Plain(x, y)}
                    '^' -> {blizzards[P2(x, y)]= Directions.north ; Plain(x, y)}
                    'v' -> {blizzards[P2(x, y)]= Directions.south ; Plain(x, y)}
                    else -> TODO()
                }
                P2(x, y) to type
            }
        }.toMap().toMutableMap()
        val xRanges: Map<Int, IntRange> = graph.entries.groupBy { it.key.y }
            .mapValues { it.value.filter { it.value !is Wall }.let { it.minOf { it.key.x } .. it.maxOf { it.key.x } } }
        val yRanges: Map<Int, IntRange> = graph.entries.groupBy { it.key.x }.mapValues {
            val list: List<MutableMap.MutableEntry<P2, Node>> = it.value.filter { node -> node.value !is Wall }
            if (list.isEmpty()) IntRange(0, 0)
            else list.minOf { it.key.y } .. list.maxOf { it.key.y }
        }.filterValues { it.last > 0 }
        for ((pos, dir) in blizzards) {
            when (dir){
                Directions.east, Directions.west -> {
                    val intRange = xRanges[pos.y]!!
                    val len = intRange.last - intRange.first + 1
                    for(x in intRange) {
                        val occ = BlizzardOccurence(len, dir.x * (x - pos.x), charForDir(dir))
                        ((graph.merge(P2(x, pos.y), Blizzards(x, pos.y)) {old, new -> if(old is Blizzards) old else new }) as Blizzards).occurences.add(occ)
                    }
                }
                Directions.north, Directions.south -> {
                    val intRange = yRanges[pos.x]!!
                    val len = intRange.last - intRange.first + 1
                    for(y in intRange) {
                        val occ = BlizzardOccurence(len, (y - pos.y) * dir.y, charForDir(dir))
                        ((graph.merge(P2(pos.x, y), Blizzards(pos.x, y)) {old, new -> if(old is Blizzards) old else new }) as Blizzards).occurences.add(occ)
                    }
                }
            }
        }

        val startingPointY = 1
        val startingPointX = input.first().indexOf(".") + 1

        val finishPointY = input.size
        val finishPointX = input.last().indexOf(".") + 1

        val destination = P2(finishPointX, finishPointY)


        val theGraph = Graph.fromIdentity<State> { state ->
            state.neighbours(graph).asSequence()
        }
        val priority: (State) -> Int = { node -> node.turn * 1000000 + node.manhatanDistance(destination) }
        val trip1 = theGraph.aStar(State(startingPointX, startingPointY, 0), destination = { pos -> pos.x == finishPointX && pos.y == finishPointY} , filter = { _-> true} ,
            priority)!!
        val trip2 = theGraph.aStar(State(finishPointX, finishPointY, trip1.last().turn), destination = { pos -> pos.x == startingPointX && pos.y == startingPointY} , filter = { _-> true} ,
            priority)!!
        val trip3 = theGraph.aStar(State(startingPointX, startingPointY, trip2.last().turn), destination = { pos -> pos.x == finishPointX && pos.y == finishPointY} , filter = { _-> true} ,
            priority)!!
        return  trip3.last().turn
    }


}


fun main() {
//    D24.solveSample(18)
//    D24.solve("day24.txt") //266
    D24.solveb("day24.txt")

}