package main.kotlin

object D3 :Solver  {

    fun solveOne(line: String): Int {
        val f = line.substring(0, line.length/2).toSet()
        val s = line.substring(line.length/2).toSet()
        val c = f.intersect(s).first()
        return if(Character.isUpperCase(c)) (c - 'A')+27
        else c - 'a' + 1
    }

    override fun solve(input: List<String>): Any {
        return input.map { solveOne(it) }.sum()
    }

    fun solveBR(input: List<String>): Int {
        if(input.isEmpty()) return 0;
        val triple = input.take(3).map { it.toSet() }
        val c = (triple.drop(1).fold(triple.first()) {a,b -> a.intersect(b)}).first()
        val score = if(Character.isUpperCase(c)) (c - 'A')+27  else c - 'a' + 1
        return score + solveBR(input.drop(3))
    }

    override fun solveb(input: List<String>): Any {
        return solveBR(input)
    }

}

fun main() {
    println(D3.solve("day3.txt"))
    println(D3.solveb("day3.txt"))
}