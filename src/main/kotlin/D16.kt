package main.kotlin

import java.util.LinkedList

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
//        fun maxFlow(map: Map<String, Valve>, remaining: Int = 30, openTunnels: Set<String> = HashSet()): Long {
//            if (remaining <= 0 || openTunnels.containsAll(map.keys)) return 0
//            return destinations.mapNotNull { map[it] }.flatMap { valve ->
//                val ifOpenSelf = if (!openTunnels.contains(this.name) && flow > 0) {
//                    val r = remaining - 1
//                    listOf(flow * r + valve.maxFlow(map, r - 1, openTunnels + name))
//                } else emptyList()
//                ifOpenSelf + valve.maxFlow(map, remaining - 1, openTunnels)
//            }.maxOrNull() ?: 0
//        }

        fun maxFlow(
            shortestDistances: Map<String, Map<String, Int>>,
            map: Map<String, Valve>,
            remaining: Int = 30,
            openTunnels: Set<String> = HashSet()
        ): Long {
            println("starting with $name, remaining: $remaining")
            if (remaining <= 0 || openTunnels.containsAll(shortestDistances.keys)) return 0L
            var (localFlow, localRem, newOpen) = if (this.flow > 0 && !openTunnels.contains(this.name)) {
                println("opening valve $name for $flow * ${remaining - 1}")
                Triple(this.flow * remaining - 1, remaining - 1, openTunnels + this.name)
            } else Triple(0, remaining, openTunnels)
            val toVisit =
                (shortestDistances.keys - this.name).filter { !openTunnels.contains(it) }.filter { map[it]!!.flow > 0 }
            if (toVisit.isEmpty()) {
                return 0L
            }
            val best = toVisit.maxBy {
                val distance = shortestDistances[this.name]!![it]!!
                (localRem - distance-1) * map[it]!!.flow
            }
            localRem -= shortestDistances[this.name]!![best]!!
//            println("moving to $best - remaining: $localRem")
            return localFlow + map[best]!!.maxFlow(shortestDistances, map, localRem, newOpen)
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
        for (name in valves.keys) {
            println("$name (${valves[name]?.flow}) -> ${shortest[name]}")
        }
        println("")
        return valves["AA"]!!.maxFlow(shortest, valves)
    }

}

fun main() {
    D16.solveSample()
}