package y2022

import common.Solver
import kotlin.math.abs

object D15 : Solver {

    data class Sensor(val x: Int, val y: Int, val beacon: Pair<Int, Int>) {
        fun distanceToBeacon() = distanceTo(beacon)
        fun distanceTo(other: Pair<Int, Int>) = abs(x - other.first) + abs(y - other.second)
        fun minMaxXFor(y2: Int): Pair<Int, Int>? {
            val diff = distanceToBeacon() - abs(y2 - y)
            return if (diff < 0) null
            else Pair(x - diff, x + diff)
        }
    }

    var y = 10

    override fun solve(input: List<String>): Any {
        val inputs = input.intsN().map { (sx, sy, bx, by) -> Sensor(sx, sy, Pair(bx, by)) }
        val beacons = inputs.map { Pair(it.x, it.y) }.toSet()
        val satelites = inputs.map { it.beacon }.toSet()
        val impossible = mutableSetOf<Pair<Int, Int>>()

        inputs.mapNotNull { it.minMaxXFor(y) }.sortedBy { it.first }.forEach {
            for (i in it.first..it.second) {
                val p = Pair(i, y)
                if (!beacons.contains(p) && !satelites.contains(p)) impossible.add(p)
            }
        }
        return impossible.size
    }

    var max = 20
    fun mergeTouching(list: List<Pair<Int, Int>>): List<Pair<Int, Int>> {
        return if (list.size < 2) list
        else {
            val f = list.first()
            val rest = list.drop(1)
            val touching = rest.firstOrNull { f.touches(it) }
            if (touching != null) {
                val merged = f.merge(touching)
                val rrest = rest - touching + merged
                mergeTouching(rrest)
            } else {
                listOf(f) + mergeTouching(rest)
            }
        }
    }

    fun tuning(p: Pair<Int, Int>) = p.first.toLong() * 4000000L + p.second.toLong()

    override fun solveb(input: List<String>): Any {
        val inputs = input.intsN().map { (sx, sy, bx, by) -> Sensor(sx, sy, Pair(bx, by)) }

        for (localY in 0..max) {
            val minMax = inputs.mapNotNull { it.minMaxXFor(localY) }.sortedBy { it.first }
            val ranges = minMax.let { mergeTouching(it) }.sortedBy { it.first }
            if (ranges.first().first > 0) {
                return tuning(Pair(ranges.first().first - 1, localY))
            } else if (ranges.last().second < max) {
                return tuning(Pair(ranges.last().second + 1, localY))
            } else if (ranges.size > 1) {
                for (i in 0 until ranges.size - 1) {
                    val p = ranges[i].second + 1
                    if (p < max) return tuning(Pair(p, localY))
                }
            }
        }
        TODO("no answer found")
    }
}

fun Any.println() = println(this)

fun main() {
    D15.solveb(
        """
        Sensor at x=2, y=18: closest beacon is at x=-2, y=15
        Sensor at x=9, y=16: closest beacon is at x=10, y=16
        Sensor at x=13, y=2: closest beacon is at x=15, y=3
        Sensor at x=12, y=14: closest beacon is at x=10, y=16
        Sensor at x=10, y=20: closest beacon is at x=10, y=16
        Sensor at x=14, y=17: closest beacon is at x=10, y=16
        Sensor at x=8, y=7: closest beacon is at x=2, y=10
        Sensor at x=2, y=0: closest beacon is at x=2, y=10
        Sensor at x=0, y=11: closest beacon is at x=2, y=10
        Sensor at x=20, y=14: closest beacon is at x=25, y=17
        Sensor at x=17, y=20: closest beacon is at x=21, y=22
        Sensor at x=16, y=7: closest beacon is at x=15, y=3
        Sensor at x=14, y=3: closest beacon is at x=15, y=3
        Sensor at x=20, y=1: closest beacon is at x=15, y=3
    """.trimIndent()
    ).println()

    D15.y = 2000000
    D15.max = 4000000
    D15.solveb("day15.txt").println()
}