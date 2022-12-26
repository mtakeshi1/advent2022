package y2022

import common.Solver
import java.util.*

object D16 : Solver {

    override fun sample(): String = """
        Valve AA has flow rate=0; tunnels lead to valves DD, II, BB
        Valve BB has flow rate=13; tunnels lead to valves CC, AA
        Valve CC has flow rate=2; tunnels lead to valves DD, BB
        Valve DD has flow rate=20; tunnels lead to valves CC, AA, EE
        Valve EE has flow rate=3; tunnels lead to valves FF, DD
        Valve FF has flow rate=0; tunnels lead to valves EE, GG
        Valve GG has flow rate=0; tunnels lead to valves FF, HH
        Valve HH has flow rate=22; tunnel leads to valve GG
        Valve II has flow rate=0; tunnels lead to valves AA, JJ
        Valve JJ has flow rate=21; tunnel leads to valve II
    """.trimIndent()

    data class Valve(val name: String, val flow: Int, val destinations: List<String>) {
        fun maxFlow(
            shortestDistances: Map<String, Map<String, Int>>,
            map: Map<String, Valve>,
            remaining: Int = 30,
            openTunnels: Set<String> = HashSet()
        ): Long {
            if (remaining <= 0 || openTunnels.containsAll(shortestDistances.keys)) return 0L
            val (localFlow, localRem, newOpen) = if (this.flow > 0 && !openTunnels.contains(this.name)) {
                Triple(this.flow * (remaining - 1), remaining - 1, openTunnels + this.name)
            } else Triple(0, remaining, openTunnels)
            val toVisit =
                (shortestDistances.keys - this.name).filter { !openTunnels.contains(it) }.filter { map[it]!!.flow > 0 }
            if (toVisit.isEmpty()) {
                return localFlow.toLong()
            }
            return toVisit.map { next ->
                val distance = shortestDistances[this.name]!![next]!!
                val nextRem = localRem - distance
                map[next]!!.maxFlow(shortestDistances, map, nextRem, newOpen)
            }.max() + localFlow
        }

    }

    fun maxFlowB(
        me: List<Pair<String, Int>>,
        elephant: List<Pair<String, Int>>,
        shortestDistances: Map<String, Map<String, Int>>,
        map: Map<String, Valve>,
        toVisit: Set<String>
    ): Int {
        fun pressureFrom(list: List<Pair<String, Int>>): Int =
            list.filter { it.second < 26 }.sumOf { (valve, turn) -> map[valve]!!.flow * (26 - turn) }

        fun totalPressure(): Int = pressureFrom(me) + pressureFrom(elephant)
        fun append(list: List<Pair<String, Int>>, to: String): List<Pair<String, Int>> =
            list + Pair(to, list.last().second + 1 + shortestDistances[list.last().first]!![to]!!)

        val localMax = totalPressure()
        if (toVisit.isEmpty() || (me.last().second >= 26 && elephant.last().second >= 26)) {
            return localMax
        }
        return toVisit.flatMap { v ->
            val newVisit = toVisit - v
            val me1 = append(me, v)
            val elephant2 = append(elephant, v)


            val left = if (me1.last().second < 26) listOf(
                maxFlowB(
                    me1,
                    elephant,
                    shortestDistances,
                    map,
                    newVisit
                )
            ) else listOf(localMax)
            val right = if (elephant2.last().second < 26) listOf(
                maxFlowB(
                    me,
                    elephant2,
                    shortestDistances,
                    map,
                    newVisit
                )
            ) else listOf(localMax)
            left + right
        }.max()
    }

    fun aStar(shortestDistances: Map<String, Map<String, Int>>, map: Map<String, Valve>): Int {
        fun flow(valve: String) = map[valve]!!.flow
        fun distance(from: String, to: String) = shortestDistances[from]!![to]!!
        val toVisit = map.values.filter { it.flow > 0 }.map { it.name }.toSet()

        data class Position(val position: String, val turn: Int, val score: Int) {
            fun moveTo(to: String): Position {
                val dist = distance(position, to)
                val nturn = turn + dist + 1
                val nscore = if (nturn >= 26) score else score + (26 - nturn) * flow(to)
                return Position(to, nturn, nscore)
            }

            fun isOverLimit() = turn >= 26
        }

        data class State(
            val me: Position,
            val elephant: Position,
            val known: Set<String> = HashSet()
        ) { //list of agents would've been better IMO
            fun score() = me.score + elephant.score
            fun stateTo(valve: String): List<State> {
                if (known.contains(valve)) return emptyList()
                val next = known + valve
                val meNextTurn = me.turn + 1 + distance(me.position, valve)
                val eleNextTurn = me.turn + 1 + distance(elephant.position, valve)
                return if(meNextTurn > eleNextTurn) {
                    listOfNotNull(elephant.takeIf { !it.isOverLimit() }?.moveTo(valve)?.let { State(me, it, next) })
                } else if(meNextTurn < eleNextTurn) {
                    listOfNotNull(me.takeIf { !it.isOverLimit() }?.moveTo(valve)?.let { State(it, elephant, next) } )
                } else {
                    listOfNotNull(elephant.takeIf { !it.isOverLimit() }?.moveTo(valve)?.let { State(me, it, next) }, me.takeIf { !it.isOverLimit() }?.moveTo(valve)?.let { State(it, elephant, next) })
                }
//                val moveMe = me.takeIf { !it.isOverLimit() }?.moveTo(valve)?.let { State(it, elephant, next) }
//                val moveEle = elephant.takeIf { !it.isOverLimit() }?.moveTo(valve)?.let { State(me, it, next) }
//                return listOfNotNull(moveMe, moveEle)
            }

            fun isOver() = (me.turn >= 26 && elephant.turn >= 26) || known.size == toVisit.size
            fun maxPossibleScore(): Int {
                if (isOver()) return score()
                val turnsLeft = maxOf(26 - me.turn, 26 - elephant.turn) - 1
                if (turnsLeft <= 0) return score()
                val remaining = map.keys - known
                val remainingScore = remaining.map {
                    val minDistance = minOf(distance(me.position, it), distance(elephant.position, it))
                    if (minDistance >= turnsLeft) 0 else (turnsLeft - minDistance) * flow(it)
                }
                val sum = remainingScore.sum()
                return sum + score()
            }
        }

        val comparator = Comparator.comparing<State, Int> { it.score() }.reversed()
        val queue = PriorityQueue(comparator)
        queue.add(State(Position("AA", 0, 0), Position("AA", 0, 0)))

        val visited = mutableSetOf<Set<Position>>()
        var max = 0
        var i = 0L
        var cut = 0L
        while (queue.isNotEmpty()) {
            val first = queue.poll()
            if (++i % 100000L == 0L) println("${Date()} iteration: ${i/1000}k queue: ${queue.size} max: $max maxPossible: ${first.maxPossibleScore()} cut: $cut")
//            if (!visited.add(setOf(first.me, first.elephant))) {
//                cut++
//                continue
//            }

            max = maxOf(first.score(), max)
            if (!first.isOver() && first.maxPossibleScore() > max) {
                toVisit.flatMap { valve -> first.stateTo(valve) }.forEach { queue.add(it) }
            } else cut++

        }
        return max
    }

    val re = Regex("Valve ([A-Z]+) has flow rate=([0-9]+); tunnels? leads? to valves? (.+)")

    fun allShortestDistances(valves: Map<String, Valve>): Map<String, Map<String, Int>> {
        return valves.values.map { valve ->
            val m = shortestDistancesFrom(valve, valves)
            valve.name to m
        }.toMap()
    }

    fun shortestDistancesFrom(from: Valve, valves: Map<String, Valve>): Map<String, Int> {
        val queue = LinkedList<Pair<String, Int>>()
        queue.add(Pair(from.name, 0))
        val visited = mutableSetOf<String>()
        val map = mutableMapOf<String, Int>()
        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            if (!visited.add(current.first)) continue
            map[current.first] = current.second
            val node = valves[current.first]!!
            node.destinations.filter { !visited.contains(it) }.forEach { queue.add(Pair(it, current.second + 1)) }
        }
        return map
    }

    fun parseValve(line: String): Valve {
        return re.matchEntire(line)?.let {
            it.destructured.let { (name, flow, tunnels) ->
                Valve(
                    name,
                    flow.toInt(),
                    tunnels.split(", ").map { it.trim() }.toList()
                )
            }
        }!!
    }

    override fun solve(input: List<String>): Any {
        val valves = input.map { parseValve(it) }.associateBy { it.name }
        val shortest = allShortestDistances(valves)
        shortest.filter { valves[it.key]!!.flow > 0 || it.key == "AA" }.forEach { (from, distances) ->
            val nd = distances.filter { (valves[it.key]!!.flow > 0 || it.key == "AA") && it.value > 0 }
            val cost = valves[from]!!.flow
            println("$from ($cost) -> $nd")
        }

        return valves["AA"]!!.maxFlow(shortest, valves)
    }

    override fun solveb(input: List<String>): Any {
        val valves = input.map { parseValve(it) }.associateBy { it.name }
        val shortest = allShortestDistances(valves)
//        val startingPoint = Pair("AA", 0)
//        val toVisit = valves.values.filter { it.flow > 0 }
//        return maxFlowB(listOf(startingPoint), listOf(startingPoint), shortest, valves, toVisit.map { it.name }.toSet())
        shortest.filter { valves[it.key]!!.flow > 0 || it.key == "AA" }.forEach { (from, distances) ->
            val nd = distances.filter { (valves[it.key]!!.flow > 0 || it.key == "AA") && it.value > 0 }
            val cost = valves[from]!!.flow
            println("$from ($cost) -> $nd")
        }
        return aStar(shortest, valves)
    }

    fun <A> combs(list: List<A>, size: Int) = list.allCombinations(size)


}

fun main() {
//    D16.solveSample()
//    println(D16.solve("day16.txt"))
    D16.solveSampleB(1707)
    println(D16.solveb("day16.txt"))
}

