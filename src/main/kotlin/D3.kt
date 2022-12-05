package main.kotlin

object D3 :Solver  {

    override fun solve(input: List<String>): Any {
        return input.sumOf { line -> score(line.toList().splitAt(line.length / 2).map { it.toSet() }.reduce { a, b -> a.intersect(b) }.first()) }
    }

    fun score(c: Char): Int = if(Character.isUpperCase(c)) (c - 'A')+27  else c - 'a' + 1

    override fun solveb(input: List<String>): Any {
        return input.chunked(3).sumOf { chunk ->
            score(chunk.map { it.toSet() }.reduce { a, b -> a.intersect(b) }.first())
        }
    }

}

fun main() {
    println(D3.solve("day3.txt") == 7850)
    println(D3.solveb("day3.txt") == 2581)
}