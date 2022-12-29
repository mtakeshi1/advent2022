package common

import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.lang.RuntimeException
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

interface Solver2022 : Solver {

    override fun fileOrString(input: String): List<String> {
        if (File(input).exists()) {
            val reader = BufferedReader(FileReader(input))
            return reader.lines().map { it }.toList()
        } else if (File("in/2022", input).exists()) {
            val reader = BufferedReader(FileReader(File("in/2022", input)))
            return reader.lines().map { it }.toList()
        }
        return input.split("\n").toList()
    }

}