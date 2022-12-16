package main.kotlin

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
        fun maxFlow(shortestDistances: Map<String, Map<String, Int>>, map: Map<String, Valve>, remaining: Int = 30, openTunnels: Set<String> = HashSet()): Long {
            if (remaining <= 0 || openTunnels.containsAll(shortestDistances.keys)) return 0L
            val (localFlow, localRem, newOpen) = if (this.flow > 0 && !openTunnels.contains(this.name)) {
                Triple(this.flow * (remaining - 1), remaining - 1, openTunnels + this.name)
            } else Triple(0, remaining, openTunnels)
            val toVisit = (shortestDistances.keys - this.name).filter { !openTunnels.contains(it) }.filter { map[it]!!.flow > 0 }
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

    /*
     * me -> if .second == 0, I'm standing on this room
     *
     */
    fun maxFlowB(movers: List<Pair<String, Int>>, shortestDistances: Map<String, Map<String, Int>>, map: Map<String, Valve>, remaining: Int = 26, openTunnels: Set<String> = HashSet(), acc: Int = 0): Int {
        if(remaining <= 0) return acc
        if(openTunnels.containsAll(shortestDistances.keys) || movers.isEmpty()) return acc + openTunnels.sumOf { map[it]!!.flow } * remaining
        val reached = movers.filter { it.second == 0 }.map { it.first }
        if(reached.isNotEmpty()) {
            val toVisit = shortestDistances.keys.filter { !openTunnels.contains(it) }.filter { map[it]!!.flow > 0 }.filter { !reached.contains(it) }
            val nextToMove = movers.filter { it.second > 0 }.map { Pair(it.first, it.second - 1) }
            val nextOpen = openTunnels + reached
            val nextRem = remaining - 1
            val nextAcc = acc + reached.sumOf { map[it]!!.flow * nextRem }

            val groupSize = reached.size.coerceAtMost(toVisit.size)
            if(groupSize == 0) return nextAcc // nothing more to visit. Not sure I need this
            return toVisit.allCombinations(groupSize).map { nextDestinations ->
                val allPairsSortedByDistance = nextDestinations.flatMap { dest -> reached.map { Pair(it, dest) } }.sortedBy { shortestDistances[it.first]!![it.second]!! }
                val nextAll: List<Pair<String, Int>> = allPairsSortedByDistance.distinctBy { it.second }.map { (from, to) -> Pair(to, shortestDistances[from]!![to]!!) } + nextToMove
                maxFlowB(nextAll, shortestDistances,map, nextRem, nextOpen, nextAcc)
            }.max()
        } else {
            val turns = movers.minOf { it.second }
            val nextMovers = movers.map { it.copy(second = it.second-1) }
            return maxFlowB(nextMovers, shortestDistances, map, remaining - turns, openTunnels, acc)
        }
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
        val valves = input.map { parseValve(it) }.map { it.name to it }.toMap()
        val shortest = allShortestDistances(valves)
        return valves["AA"]!!.maxFlow(shortest, valves)
    }

    override fun solveb(input: List<String>): Any {
        val valves = input.map { parseValve(it) }.associateBy { it.name }
        val shortest = allShortestDistances(valves)
        return maxFlowB(listOf(Pair("AA", 0), Pair("AA", 0)), shortest, valves)
    }

    fun <A> combs(list: List<A>, size: Int) = list.allCombinations(size)


}

fun main() {
//    D16.solveSample()
//    println(D16.solve("day16.txt"))
    D16.solveSampleB()
}

