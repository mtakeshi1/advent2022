package main.kotlin

object D17 : Solver {

    val floor = 0
    val leftWall = 0
    val rightWall = 8

    //TODO add shapes, test movements, test spawn

    data class Shape(val points: Set<Pair<Int, Int>>) {
        fun leftMost() = points.minBy { it.first }
        fun bottom() = points.minBy { it.second }

        fun colidesWith(other: Shape) = this.points.intersect(other.points).isNotEmpty()

        fun moveRight(amount: Int = 1): Shape = Shape(points.map { it.copy(first = it.first+amount) }.toSet())
        fun moveLeft(amount: Int = 1): Shape = moveRight(-amount)
        fun moveDown(amount: Int = 1): Shape = Shape(points.map { it.copy(second = it.second-amount) }.toSet())
        fun moveUp(amount: Int = 1): Shape = moveDown(-amount)

        fun isValid(otherShapes: List<Shape>) = points.all { it.first > leftWall && it.first < rightWall && it.second > floor } && otherShapes.none { it.colidesWith(this) }

        fun spawn(otherShapes: List<Shape>): Shape {
            val leftDiff = 3 - leftMost().first
            val highest = otherShapes.flatMap { it.points.map { it.second } }.maxOrNull() ?: floor
            return moveRight(leftDiff).moveUp(highest + 4)
        }
    }

    override fun sample(): String = ">>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>"

    override fun solve(input: List<String>): Any {
        val pattern = input.toTypedArray()
        val wind = generateSequence(1) { it + 1}.map { pattern[it % pattern.size] }
        return super.solve(input)
    }

}


fun main() {
    D17.solveSample(3068)
}