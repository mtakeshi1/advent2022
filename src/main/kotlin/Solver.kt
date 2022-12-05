package main.kotlin

import java.io.BufferedReader
import java.io.File
import java.io.FileReader

interface Solver {

    fun List<String>.splitOnEmpty(): Pair<List<String>, List<String>> {
        val first = this.takeWhile { it.isNotEmpty() }
        val second = this.drop(first.size + 1)
        return Pair(first, second)
    }

    fun List<String>.ints2(): List<Pair<Int, Int>> {
        val re = Regex("(-?[0-9]+)")
        return this.map {
            val r: List<MatchResult> = re.findAll(it).toList()
            Pair(r[0].value.toInt(), r[1].value.toInt())
        }
    }

    fun List<String>.ints3(): List<Triple<Int, Int, Int>> {
        val re = Regex("(-?[0-9]+)")
        return this.map {
            val r: List<MatchResult> = re.findAll(it).toList()
            Triple(r[0].value.toInt(), r[1].value.toInt(), r[2].value.toInt())
        }
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