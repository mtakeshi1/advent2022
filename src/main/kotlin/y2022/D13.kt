package y2022

import common.Solver2022
import kotlin.math.min

object D13 : Solver2022 {

    interface Signal : Comparable<Signal> {
        override fun compareTo(other: Signal): Int
        fun lessThan(other: Signal): Boolean = this < other
        fun asList(): PList
    }

    data class Value(val v: Int) : Signal {
        override fun compareTo(other: Signal): Int {
            return when (other) {
                is Value -> this.v - other.v
                else -> asList().compareTo(other)
            }
        }

        override fun asList(): PList = PList(listOf(this))
    }

    data class PList(val entries: List<Signal>) : Signal {
        override fun compareTo(other: Signal): Int {
            return when (other) {
                is Value -> this.compareTo(other.asList())
                is PList -> compareToList(other.entries)
                else -> TODO("Not yet implemented")
            }
        }

        fun compareToList(other: List<Signal>): Int {
            for (i in 0 until min(this.entries.size, other.size)) {
                val c = this.entries[i].compareTo(other[i])
                if (c != 0) return c
            }
            return this.entries.size - other.size
        }

        override fun asList(): PList = this
    }

    object EndOfInput : Signal {
        override fun compareTo(other: Signal): Int = TODO()
        override fun asList(): PList = TODO()
    }

    fun parseNumber(line: String, start: Int): Pair<Signal, Int> {
        var c = start
        while (line[c] in '0'..'9') c++
        val v = line.substring(start, c).toInt()
        return Pair(Value(v), c)
    }

    fun parseList(line: String, start: Int): Pair<Signal, Int> {
        val acc: MutableList<Signal> = ArrayList()
        var nextOffset = start + 1
        while (line[nextOffset] != ']') {
            if (line[nextOffset] == ',' || line[nextOffset] == ' ') {
                nextOffset++
            } else {
                val (signal, nextIndex) = parsePacket(line, nextOffset)
                acc.add(signal)
                nextOffset = nextIndex
            }
        }
        return Pair(PList(acc), nextOffset + 1)
    }

    fun parsePacket(line: String, start: Int = 0): Pair<Signal, Int> {
        return if (line[start] == '[') {
            parseList(line, start)
        } else if (line[start] in '0'..'9') {
            parseNumber(line, start)
        } else if (start < line.length) parsePacket(line, start + 1)
        else Pair(EndOfInput, start + 1)
    }

    fun parse(line: String) = parsePacket(line).first

    override fun solve(input: List<String>): Any {
        return input.partitionOnEmpty().withIndex().map {
            Pair(it.index + 1, parse(it.value[0]).compareTo(parse(it.value[1])))
        }.filter { it.second < 0 }.sumOf { it.first }
    }

    override fun solveb(input: List<String>): Any {
        val div1 = parse("[[2]]")
        val div2 = parse("[[6]]")
        val sorted = (input.filter { it.isNotEmpty() }.map { parse(it) } + div1 + div2).sorted()
        return (sorted.indexOf(div1) + 1) * (sorted.indexOf(div2) + 1)
    }
}

fun main() {
    println(D13.parse("[[[]]]").compareTo(D13.parse("[[]]")))
//    println(D12.parsePacket("[1,[2,[3,[4,[5,6,7]]]],8,9]"))
    println(
        D13.solve(
            """
        [1,1,3,1,1]
        [1,1,5,1,1]

        [[1],[2,3,4]]
        [[1],4]

        [9]
        [[8,7,6]]

        [[4,4],4,4]
        [[4,4],4,4,4]

        [7,7,7,7]
        [7,7,7]

        []
        [3]

        [[[]]]
        [[]]

        [1,[2,[3,[4,[5,6,7]]]],8,9]
        [1,[2,[3,[4,[5,6,0]]]],8,9]
    """.trimIndent()
        )
    )

    println(D12.solve("day12.txt"))
    println(D12.solveb("day12.txt"))
}