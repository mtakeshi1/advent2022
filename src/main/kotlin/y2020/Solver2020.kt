package advent2020

import common.Solver
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

interface Solver2020 : Solver {
    override fun fileOrString(input: String): List<String> {
        if (File(input).exists()) {
            val reader = BufferedReader(FileReader(input))
            return reader.lines().map { it }.toList()
        } else if (File("in/2020", input).exists()) {
            val reader = BufferedReader(FileReader(File("in/2020", input)))
            return reader.lines().map { it }.toList()
        }
        return input.split("\n").toList()
    }
}