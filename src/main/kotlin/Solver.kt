package main.kotlin

import java.io.BufferedReader
import java.io.File
import java.io.FileReader

interface Solver {

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