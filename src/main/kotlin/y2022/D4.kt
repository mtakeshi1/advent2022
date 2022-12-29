package y2022

import common.Solver2022

object D4 : Solver2022 {

    data class Range(val from: Int, val to: Int) {
        fun contains(other: Range): Boolean = from <= other.from && to >= other.to

        fun overlaps(other: Range): Boolean {
            return if (this.from <= other.from) this.to >= other.from
            else other.from <= this.from && other.to >= this.from
        }

        companion object {
            fun overlaps(line: String): Boolean {
                val (l, r) = parse(line)
                return l.overlaps(r)
            }

            fun contained(line: String): Boolean {
                val (l, r) = parse(line)
                return l.contains(r) || r.contains(l)
            }

            fun parse(line: String): Pair<Range, Range> {
                return Regex("([0-9]+)-([0-9]+),([0-9]+)-([0-9]+)").matchEntire(line)?.let {
                    Pair(
                        Range(it.groupValues[1].toInt(), it.groupValues[2].toInt()),
                        Range(it.groupValues[3].toInt(), it.groupValues[4].toInt())
                    )
                }!!
            }
        }

    }


    override fun solve(input: List<String>): Any {
        return input.count { Range.contained(it) }
    }

    override fun solveb(input: List<String>): Any {
        return input.count { Range.overlaps(it) }
    }


}

fun main() {
    println(
        D4.solveb(
            """
        2-4,6-8
        2-3,4-5
        5-7,7-9
        2-8,3-7
        6-6,4-6
        2-6,4-8
    """.trimIndent()
        )
    )

    println(D4.solve("day4.txt") == 580)
    println(D4.solveb("day4.txt") == 895)
}