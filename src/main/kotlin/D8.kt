package main.kotlin

object D8 : Solver {

    fun checkVisibility(matrix: List<List<Int>>, known: MutableSet<Pair<Int, Int>>, position: Pair<Int, Int>) {
        if(!known.contains(position)) {
            if(visibleFrom(position, matrix, Pair(1,0)) ||
                visibleFrom(position, matrix, Pair(-1,0)) ||
                visibleFrom(position, matrix, Pair(0, 1)) ||
                visibleFrom(position, matrix, Pair(0, -1))) {
                known.add(position)
            }
        }
    }

    private fun visibleFrom(position: Pair<Int, Int>, matrix: List<List<Int>>, direction: Pair<Int, Int>): Boolean {
        var next = position + direction
        val height = matrix[position.first][position.second]
        while (inside(next, matrix)) {
            if(matrix[next.first][next.second] >= height) return false
            next += direction
        }
        return true
    }

    override fun solve(input: List<String>): Any {
        val matrix = input.map {line -> line.map { it.code - '0'.code } }
        val visible: MutableSet<Pair<Int, Int>> = HashSet()
        for(x in matrix.indices) {
            for (y in matrix[x].indices) {
                checkVisibility(matrix, visible, Pair(x, y))
            }
        }
        return visible.size
    }

    private fun countFrom(position: Pair<Int, Int>, matrix: List<List<Int>>, direction: Pair<Int, Int>): Long {
        var next = position + direction
        val height = matrix[position.first][position.second]
        var c = 0L
        while (true) {
            if(!inside(next, matrix)) return c
            c++
            if(matrix[next.first][next.second] >= height) return c
            next += direction
        }
    }

    private fun inside(
        next: Pair<Int, Int>,
        matrix: List<List<Int>>
    ) = next.first >= 0 && next.first < matrix.size && next.second >= 0 && next.second < matrix[next.first].size

    fun count(position: Pair<Int, Int>, matrix: List<List<Int>>): Long = countFrom(position, matrix, Pair(1, 0)) * countFrom(position, matrix, Pair(-1, 0)) * countFrom(position, matrix, Pair(0, 1)) * countFrom(position, matrix, Pair(0, -1))

    override fun solveb(input: List<String>): Any {
        val matrix = input.map {line -> line.map { it.code - '0'.code } }
        return matrix.indices.flatMap { x ->
            matrix[x].indices.map { y ->
                count(Pair(x, y), matrix)
            }
        }.max()
    }

}

fun main() {
    println(D8.solve("""
        30373
        25512
        65332
        33549
        35390
    """.trimIndent()))

    println(D8.solve("day8.txt"))
    println(D8.solveb("""
        30373
        25512
        65332
        33549
        35390
    """.trimIndent()))

    println(D8.solveb("day8.txt"))

}