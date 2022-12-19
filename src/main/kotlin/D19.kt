package main.kotlin


object D19 : Solver {

    enum class Resource {
        ore, clay, obsidian, geode, nothing
    }

    interface Robot {
        fun products(): Map<Resource, Int>
        fun requirements(): Map<Resource, Int>
    }

    data class PlainRobot(val requirements: Map<Resource, Int>, val produces: Resource) : Robot {
        fun canProduce(resources: Map<Resource, Int>) = requirements.all { resources.getOrDefault(it.key,0) >= it.value }
        override fun products(): Map<Resource, Int> = mapOf(produces to 1)
        override fun requirements(): Map<Resource, Int> = this.requirements
        override fun toString(): String = "Robot {%s} ".format(produces)
    }

    object DoNothingRobot : Robot {
        override fun products(): Map<Resource, Int> = emptyMap()
        override fun requirements(): Map<Resource, Int> = emptyMap()
    }

    data class BluePrint(val id: Int, val robotReceipts: List<PlainRobot>) {

        fun bestMove(resources: Map<Resource, Int> = emptyMap(), turnsLeft: Int = 24, robots: Map<Resource, Int> = mapOf(Resource.ore to 1)): Map<Resource, Int> {
            if(turnsLeft <= 0) {
                return resources
            }
            val canBuild = robotReceipts.filter { it.canProduce(resources) } + DoNothingRobot
            if(canBuild.isEmpty()) return bestMove(resources.mergePlus(robots), turnsLeft - 1, robots)
            return canBuild.map { robot ->
                val leftOver = resources.mergeMinus(robot.requirements()).mergePlus(robots)
                bestMove(leftOver, turnsLeft-1, robots.mergePlus(robot.products()))
            }.maxBy { it.getOrDefault(Resource.geode, 0) }
        }
    }

    fun parseRobot(robotParts: String): PlainRobot {
        //Each ore robot costs 4 ore [and 11 clay]
        val (type, costs) = robotParts.split("costs").let { Pair(Resource.valueOf(it[0].split(" ")[1]), it[1].split("and").map { it.trim() }) }
        val map = costs.map { it.split(" ") }.map {  Resource.valueOf(it[1]) to it[0].toInt() }.toMap()
        return PlainRobot(map, type)
    }

    fun parseBlueprint(line: String): BluePrint {
        val (id, robots) = line.split(":").map{it.trim()}
        val eachRobot = robots.split(".").filter { it.isNotEmpty() }.map { parseRobot(it.trim()) }.sortedBy { it.produces }.reversed()
        return BluePrint(id.split(" ")[1].toInt(), eachRobot)
    }


    override fun sample(): String = """
    Blueprint 1: Each ore robot costs 4 ore. Each clay robot costs 2 ore. Each obsidian robot costs 3 ore and 14 clay. Each geode robot costs 2 ore and 7 obsidian.
    """
//Blueprint 2: Each ore robot costs 2 ore. Each clay robot costs 3 ore. Each obsidian robot costs 3 ore and 8 clay. Each geode robot costs 3 ore and 12 obsidian.
    override fun solve(input: List<String>): Any {
        val best = input.map { it.trim() }.filter { it.isNotEmpty() }.map { parseBlueprint(it) }
            .map { Pair(it, it.bestMove()) }
//            .map { it.second.getOrDefault(Resource.geode, 0) }
//            .maxBy { it.bestMove().getOrDefault(Resource.geode, 0) }
        val map = best.map { it.first.id * it.second.getOrDefault(Resource.geode, 0) }
        return map.sum()
    }

}

fun main() {

    D19.solveSample(9)

}