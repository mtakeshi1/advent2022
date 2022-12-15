package main.kotlin

import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

interface Solver {

    operator fun Pair<Int, Int>.plus(other: Pair<Int, Int>) = Pair(this.first + other.first, this.second + other.second)
    fun Pair<Int, Int>.distanceFrom(other: Pair<Int, Int>): Int = abs(this.first - other.first) + abs(this.second - other.second)
    fun Pair<Int, Int>.touches(other: Pair<Int, Int>): Boolean {
        return (this.first <= other.first && this.second >= other.first) ||
                (this.first >= other.first && other.second >= this.first)
    }

    fun Pair<Int, Int>.merge(other: Pair<Int, Int>): Pair<Int, Int> {
        return Pair(min(this.first, other.first), max(this.second, other.second))
    }

    fun List<String>.splitOnEmpty(): Pair<List<String>, List<String>> {
        val first = this.takeWhile { it.isNotEmpty() }
        val second = this.drop(first.size + 1)
        return Pair(first, second)
    }

    fun List<String>.partitionOnEmpty(): List<List<String>> {
        val head = listOf(this.takeWhile { it.isNotEmpty() })
        val rest = this.drop(head[0].size+1)
        return if(rest.isEmpty()) head else head + rest.partitionOnEmpty()
    }

    fun List<String>.ints(): List<Int> = intsN().map { it.first() }
    fun List<String>.ints2(): List<Pair<Int, Int>>  = intsN().map { Pair(it[0], it[1]) }
    fun List<String>.ints3(): List<Triple<Int, Int, Int>> = intsN().map { Triple(it[0], it[1], it[1]) }

    fun List<String>.intsN(): List<List<Int>> {
        val re = Regex("(-?[0-9]+)")
        return this.map { re.findAll(it).toList().map { found -> found.value.toInt() } }
    }

    fun <A> List<List<A>>.transposed(): List<List<A>> {
        return this[0].indices.map { col -> this.indices.map { row -> this[row][col] } }
    }

    fun List<String>.join(): String = this.joinToString(separator = "")

    fun <A> List<A>.splitAt(index: Int): List<List<A>> = listOf(this.take(index), this.drop(index))

    fun solveb(input: String): Any {
        if (File(input).exists()) {
            val reader = BufferedReader(FileReader(input))
            return solveb(reader.lines().map { it }.toList())
        } else if (File("in", input).exists()) {
            val reader = BufferedReader(FileReader(File("in", input)))
            return solveb(reader.lines().map { it }.toList())
        } else if (File("advent2022/in", input).exists()) {
            val reader = BufferedReader(FileReader(File("advent2022/in", input)))
            return solveb(reader.lines().map { it }.toList())
        }
        return solveb(input.split("\n").toList())
    }

    fun solveb(input: List<String>): Any = solveIb(input.map { Integer.parseInt(it) })
    fun solveIb(input: List<Int>): Any = TODO()

    fun solve(input: String): Any {
        if (File(input).exists()) {
            val reader = BufferedReader(FileReader(input))
            return solve(reader.lines().map { it }.toList())
        } else if (File("in", input).exists()) {
            val reader = BufferedReader(FileReader(File("in", input)))
            return solve(reader.lines().map { it }.toList())
        } else if (File("advent2022/in", input).exists()) {
            val reader = BufferedReader(FileReader(File("advent2022/in", input)))
            return solve(reader.lines().map { it }.toList())
        }
        return solve(input.split("\n").toList())
    }

    fun solve(input: List<String>): Any = solveI(input.map { Integer.parseInt(it) })
    fun solveI(input: List<Int>): Any = TODO()
}