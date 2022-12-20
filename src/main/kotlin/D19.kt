package main.kotlin

import main.kotlin.D19.mergeMinus
import main.kotlin.D19.mergePlus
import java.util.Comparator
import java.util.Date
import java.util.EnumMap
import java.util.LinkedHashSet
import java.util.PriorityQueue


object D19 : Solver {

    override fun sample(): String = """
    Blueprint 1: Each ore robot costs 4 ore. Each clay robot costs 2 ore. Each obsidian robot costs 3 ore and 14 clay. Each geode robot costs 2 ore and 7 obsidian.
    Blueprint 2: Each ore robot costs 2 ore. Each clay robot costs 3 ore. Each obsidian robot costs 3 ore and 8 clay. Each geode robot costs 3 ore and 12 obsidian.
    """

    fun parseRobot(robotParts: String): PlainRobot {
        //Each ore robot costs 4 ore [and 11 clay]
        val (type, costs) = robotParts.split("costs")
            .let { Pair(Resource.valueOf(it[0].split(" ")[1]), it[1].split("and").map { it.trim() }) }
        val map = costs.map { it.split(" ") }.map { Resource.valueOf(it[1]) to it[0].toInt() }.toMap()
        return PlainRobot(EnumMap(map), type)
    }

    fun parseBlueprint(line: String): BluePrint {
        val (id, robots) = line.split(":").map { it.trim() }
        val eachRobot =
            robots.split(".").filter { it.isNotEmpty() }.map { parseRobot(it.trim()) }.sortedBy { it.produces }
                .reversed()
        return BluePrint(id.split(" ")[1].toInt(), eachRobot)
    }

    enum class Resource {
        ore, clay, obsidian, geode
    }

    interface Robot {
        fun products(): Map<Resource, Int>
        fun requirements(): Map<Resource, Int>
    }

    data class PlainRobot(val requirements: Map<Resource, Int>, val produces: Resource) : Robot {
        fun canManufacture(resources: Map<Resource, Int>) =
            requirements.all { resources.getOrDefault(it.key, 0) >= it.value }

        override fun products(): Map<Resource, Int> = mapOf(produces to 1)
        override fun requirements(): Map<Resource, Int> = this.requirements
        override fun toString(): String = "Robot {%s} ".format(produces)
    }

    object DoNothingRobot : Robot {
        override fun products(): Map<Resource, Int> = emptyMap()
        override fun requirements(): Map<Resource, Int> = emptyMap()
    }

    data class State(
        val resources: Map<Resource, Int>,
        val robots: Map<Resource, Int>,
        val turnsLeft: Int,
        val robotReceipts: List<PlainRobot>
    ) {

        fun scoreFor(resource: Resource) =
            resources.getOrDefault(resource, 0) + (robots.getOrDefault(resource, 0) * turnsLeft)

        val geodeReceipt = robotReceipts.find { it.produces == Resource.geode }

        val geodesScore = (maxGeodesPossible() * 10000) + (scoreFor(Resource.geode) * 20000)

        fun maxGeodesPossible(): Int {
            val geodesFactory = robots.getOrDefault(Resource.geode, 0)
            return (0 until turnsLeft).map { geodesFactory + it }.sum() + resources.getOrDefault(Resource.geode, 0)
        }

        fun isBetterThan(other: State): Boolean {
            if (this.turnsLeft > other.turnsLeft) return false
            if (this.resources.keys.containsAll(other.resources.keys) && this.resources.all { (resource, amount) ->
                    other.resources.getOrDefault(
                        resource,
                        0
                    ) <= amount
                }) {
                if (this.robots.keys.containsAll(other.robots.keys) && this.robots.all { (resource, amount) ->
                        other.robots.getOrDefault(
                            resource,
                            0
                        ) <= amount
                    }) {
                    val foundWorse = this.turnsLeft <= other.turnsLeft
                    return foundWorse
                }
            }
            return false
        }

        fun buildableRobots() = robotReceipts.filter { it.canManufacture(resources) }
    }

    data class BluePrint(val id: Int, val robotReceipts: List<PlainRobot>) {
        fun bestMove(
            resources: Map<Resource, Int> = emptyMap(),
            turnsLeft: Int = 24,
            robots: Map<Resource, Int> = mapOf(Resource.ore to 1)
        ): Map<Resource, Int> {
            if (turnsLeft <= 0) {
                return resources
            }
            val canBuild = robotReceipts.filter { it.canManufacture(resources) } + DoNothingRobot
            if (canBuild.isEmpty()) return bestMove(resources.mergePlus(robots), turnsLeft - 1, robots)
            return canBuild.map { robot ->
                val leftOver = resources.mergeMinus(robot.requirements()).mergePlus(robots)
                bestMove(leftOver, turnsLeft - 1, robots.mergePlus(robot.products()))
            }.maxBy { it.getOrDefault(Resource.geode, 0) }
        }

        fun bestMoveBFS(minutes: Int = 24): Int {
            "${Date()} Starting id: $id".format(id).println()
            val queue = PriorityQueue<State>(Comparator.comparing<State?, Int?> { it.geodesScore }.reversed())
            val visited = LinkedHashSet<State>()
//            val visitedMap = Array(minutes + 2) { LinkedHashSet<State>() }
            queue.add(State(emptyMap(), EnumMap(mapOf(Resource.ore to 1)), minutes, this.robotReceipts))
            var max = 0
            var i = 0
            while (queue.isNotEmpty()) {
                val next = queue.poll()
                if ((++i % 500000) == 0) {
                    val num = visited.size
//                    val num = visitedMap.map { it.size }.sum()
                    "${Date()} id: $id queue: ${queue.size}, turnsLeft: ${next.turnsLeft}, visited: $num, max: $max, iteration: $i".println()
                }
                if(next.turnsLeft > 1 && visited.asSequence().takeWhile { it.turnsLeft <= next.turnsLeft }.any { it.isBetterThan(next) } ) continue
//                if(next.turnsLeft > 1 && visitedMap.computeIfAbsent(next.turnsLeft - 1) {LinkedHashSet()}.any { it.isBetterThan(next) } ) continue
                if (next.maxGeodesPossible() < max) continue
                max = maxOf(max, next.resources.getOrDefault(Resource.geode, 0))
                if (next.turnsLeft == 1) {
                    max = maxOf(max, next.resources.mergePlus(next.robots).getOrDefault(Resource.geode, 0))
                    continue
                } else if (next.turnsLeft <= 2 && !next.robots.containsKey(Resource.obsidian)) {
                    continue
                } else if (next.turnsLeft == 2) {
                    val robot = robotReceipts.find { it.produces == Resource.geode }
                        ?.takeIf { it.canManufacture(next.resources) } ?: DoNothingRobot
                    val nextResources = next.resources.mergeMinus(robot.requirements()).mergePlus(next.robots)
                    val nextRobots = next.robots.mergePlus(robot.products())
                    queue.add(State(nextResources, nextRobots, next.turnsLeft - 1, this.robotReceipts))
//                } else if(next.turnsLeft == 3) {
//                    val desired = robotReceipts.find { it.produces == Resource.geode }!!.requirements.keys + Resource.geode
//                    val buildableRobots = robotReceipts.filter { desired.contains(it.produces) } + DoNothingRobot
//                    buildableRobots.forEach { robot ->
//                        val nextResources = next.resources.mergeMinus(robot.requirements()).mergePlus(next.robots)
//                        val nextRobots = next.robots.mergePlus(robot.products())
//                        queue.add(State(nextResources, nextRobots, next.turnsLeft - 1))
//                    }
                } else {
                    if(!visited.add(next)) continue
//                    if(!visitedMap.computeIfAbsent(next.turnsLeft) {LinkedHashSet()}.add(next)) continue
                    val cannotBuildRobots = robotReceipts.filter { !it.canManufacture(next.resources) }
                    val buildableRobots =
                        if (cannotBuildRobots.any { next.robots.keys.containsAll(it.requirements.keys) }) {
                            next.buildableRobots() + DoNothingRobot
                        } else {
                            next.buildableRobots()
                        }

                    buildableRobots.find { it.products().containsKey(Resource.geode) }?.let { robot ->
                        val nextResources = next.resources.mergeMinus(robot.requirements()).mergePlus(next.robots)
                        val nextRobots = next.robots.mergePlus(robot.products())
                        val nextNext = State(nextResources, nextRobots, next.turnsLeft - 1, this.robotReceipts)
//                        if (next.turnsLeft > 1 && visitedMap[next.turnsLeft - 1].any { it.isBetterThan(next) }) {
//                            //do nothing for now
//                        } else if (visitedMap[nextNext.turnsLeft].add(nextNext)) queue.add(nextNext)
                        queue.add(nextNext)
                    } ?: buildableRobots.forEach { robot ->
                        val nextResources = next.resources.mergeMinus(robot.requirements()).mergePlus(next.robots)
                        val nextRobots = next.robots.mergePlus(robot.products())
                        val nextNext = State(nextResources, nextRobots, next.turnsLeft - 1, this.robotReceipts)
//                        if (next.turnsLeft > 1 && visitedMap[next.turnsLeft - 1].any { it.isBetterThan(next) }) {
                            //do nothing for now
//                        } else if (visitedMap[nextNext.turnsLeft].add(nextNext)) queue.add(nextNext)
                        queue.add(nextNext)

                    }
                }

            }
            "${Date()} Finished id: $id with max: $max, totalIter: $i".println()
            return max
        }

        val maxRequirements =
            Resource.values().map { res -> Pair(res, robotReceipts.map { it.requirements.getOrDefault(res, 0) }.max()) }.toMap().filterValues { it > 0 }

        fun bestMoveBFS2(minutes: Int = 24): Int {

            "${Date()} Starting id: $id".format(id).println()
            val visitedSets = Array(minutes + 2) { ArrayList<State>() }
            visitedSets[minutes].add(State(emptyMap(), EnumMap(mapOf(Resource.ore to 1)), minutes, this.robotReceipts))
            var turn = minutes
            var max = 0
            var i = 0
            while (turn > 1) {
//                visitedSets[turn+1].clear()
                val states = visitedSets[turn]
                println("${Date()} turn: $turn, toCheck: ${states.size}, iter: $i, max: $max")
                states.forEach { state ->
                    max = maxOf(state.resources.getOrDefault(Resource.geode, 0))
                    if (state.maxGeodesPossible() >= max) {
                        val cannotBuildRobots = robotReceipts.filter { !it.canManufacture(state.resources) }
                        val buildableRobots =
                            if (cannotBuildRobots.any { state.robots.keys.containsAll(it.requirements.keys) }) {
                                state.buildableRobots() + DoNothingRobot
                            } else {
                                state.buildableRobots()
                            }
                        buildableRobots.forEach { robot ->
                            val nextResources = state.resources.mergeMinus(robot.requirements()).mergePlus(state.robots)
                            val nextRobots = state.robots.mergePlus(robot.products())
                            val nextNext = State(nextResources, nextRobots, state.turnsLeft - 1, this.robotReceipts)
                            val canAdvance = //true
                                (robot.products().keys - Resource.geode).none { nextResources.getOrDefault(it, 0) > maxRequirements.getOrDefault(it, 0) }
                            if (canAdvance && visitedSets[nextNext.turnsLeft].none { it.isBetterThan(nextNext) }) visitedSets[nextNext.turnsLeft].add(
                                nextNext
                            )
                            i++
                        }
                    }

                }
                turn--
            }
            max = visitedSets[1].map { next -> next.resources.mergePlus(next.robots).getOrDefault(Resource.geode, 0) }.max()
            "${Date()} Finished id: $id with max: $max, totalIter: $i".println()
            return max
        }

    }


    //Blueprint 2: Each ore robot costs 2 ore. Each clay robot costs 3 ore. Each obsidian robot costs 3 ore and 8 clay. Each geode robot costs 3 ore and 12 obsidian.
    override fun solve(input: List<String>): Any {
        val best = input.map { it.trim() }.filter { it.isNotEmpty() }.map { parseBlueprint(it) }
            .map { Pair(it, it.bestMoveBFS()) }
//            .map { it.second.getOrDefault(Resource.geode, 0) }
//            .maxBy { it.bestMove().getOrDefault(Resource.geode, 0) }
        best.forEach { println("${it.first.id} -> ${it.second} = ${it.first.id * it.second}") }
        val map = best.map { it.first.id * it.second }
        return map.sum()
    }


    override fun solveb(input: List<String>): Any {
        val best = input.map { it.trim() }.filter { it.isNotEmpty() }.take(3).map { parseBlueprint(it) }
            .map { it.bestMoveBFS(32).toLong() }
//            .map { it.second.getOrDefault(Resource.geode, 0) }
//            .maxBy { it.bestMove().getOrDefault(Resource.geode, 0) }
        return best.reduce { a, b -> a * b }
    }

}

fun main() {

    D19.solveSample(33)
//    D19.solve("in/day19.txt").println()
//D19.solveSampleB(62*56L)
}