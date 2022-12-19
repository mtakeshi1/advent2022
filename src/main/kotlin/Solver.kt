package main.kotlin

import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.lang.RuntimeException
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

interface Solver {

    fun sample(): String = ""

    fun solveSample() = println(solve(sample()))
    fun solveSample(expected: Any) {
        val message = solve(sample())
        if (message != expected) throw RuntimeException("expected $expected but got: $message")
        println(message)
    }

    fun solveSampleB() = println(solveb(sample()))
    fun solveSampleB(expected: Any) {
        val message = solveb(sample())
        if (message != expected) throw RuntimeException("expected $expected but got: $message")
        println(message)
    }
    fun <A> List<A>.allPairs(): List<Pair<A, A>> {
        return if (this.size <= 1) emptyList()
        else {
            val f = this.first()
            val r = this.drop(1)
            r.map { Pair(f, it) } + r.allPairs()
        }
    }

    fun wrappingSequence(max: Int): Sequence<Int> =  generateSequence(0) { if(it + 1 >= max) 0 else it+1 }

    fun <A> infiniteSequenceOf(list: List<A>): Sequence<A> = wrappingSequence(list.size).map { list[it % list.size] }


    fun <A> List<A>.allCombinations(size: Int): List<List<A>> {
        if (this.isEmpty() || this.size < size) return emptyList()
        else if (size == 1) return this.map { listOf(it) }
        else {
            val remaining =
                this.indices.map { Pair(this[it], this.drop(it + 1)) }.filter { it.second.size >= (size - 1) }
//            println(remaining)
            return remaining.flatMap { (el, rest) ->
                rest.allCombinations(size - 1).map { listOf(el) + it }
            }
        }
    }

    operator fun Pair<Int, Int>.plus(other: Pair<Int, Int>) = Pair(this.first + other.first, this.second + other.second)
    fun Pair<Int, Int>.distanceFrom(other: Pair<Int, Int>): Int =
        abs(this.first - other.first) + abs(this.second - other.second)

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
        val rest = this.drop(head[0].size + 1)
        return if (rest.isEmpty()) head else head + rest.partitionOnEmpty()
    }

    fun List<String>.ints(): List<Int> = intsN().map { it.first() }
    fun List<String>.ints2(): List<Pair<Int, Int>> = intsN().map { Pair(it[0], it[1]) }
    fun List<String>.ints3(): List<Triple<Int, Int, Int>> = intsN().map { Triple(it[0], it[1], it[2]) }

    fun List<String>.intsN(): List<List<Int>> {
        val re = Regex("(-?[0-9]+)")
        return this.map { re.findAll(it).toList().map { found -> found.value.toInt() } }
    }

    fun <A> List<List<A>>.transposed(): List<List<A>> {
        return this[0].indices.map { col -> this.indices.map { row -> this[row][col] } }
    }

    fun List<String>.join(): String = this.joinToString(separator = "")

    fun <A> List<A>.splitAt(index: Int): List<List<A>> = listOf(this.take(index), this.drop(index))

    fun fileOrString(input: String): List<String> {
        if (File(input).exists()) {
            val reader = BufferedReader(FileReader(input))
            return reader.lines().map { it }.toList()
        } else if (File("in", input).exists()) {
            val reader = BufferedReader(FileReader(File("in", input)))
            return reader.lines().map { it }.toList()
        } else if (File("advent2022/in", input).exists()) {
            val reader = BufferedReader(FileReader(File("advent2022/in", input)))
            return reader.lines().map { it }.toList()
        }
        return input.split("\n").toList()

    }

    fun solveb(input: String): Any = solveb(fileOrString(input)).apply { println(this) }

    fun solveb(input: List<String>): Any = solveIb(input.map { Integer.parseInt(it) })
    fun solveIb(input: List<Int>): Any = TODO()

    fun solve(input: String): Any = solve(fileOrString(input)).apply { println(this) }

    fun solve(input: List<String>): Any = solveI(input.map { Integer.parseInt(it) })
    fun solveI(input: List<Int>): Any = TODO()
}