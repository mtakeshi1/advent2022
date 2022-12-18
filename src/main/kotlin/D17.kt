package main.kotlin

object D17 : Solver {

    val floor = 0
    val leftWall = 0
    val rightWall = 8

    //TODO add shapes, test movements, test spawn

    fun p(x: Int, y: Int) = Pair(x, y)

    val shapeMinus = Shape(setOf(p(0, 0), p(1, 0), p(2, 0), p(3, 0)))
    val shapePlus = Shape(setOf(p(0, 0), p(1, 0), p(-1, 0), p(0, 1), p(0, -1)))
    val shapeL = Shape(setOf(p(2, 0), p(2, -1), p(2, -2), p(0, -2), p(1, -2)))
    val shapeI = Shape(setOf(p(0, 0), p(0, -1), p(0, -2), p(0, -3)))
    val shapeSq = Shape(setOf(p(0, 0), p(0, -1), p(1, 0), p(1, -1)))

    fun allShapes() = infiniteSequenceOf(listOf(shapeMinus, shapePlus, shapeL, shapeI, shapeSq))

    fun printShapes(list: List<Shape>) {
        if (list.isEmpty()) return
        val minX = list.flatMap { it.points }.map { it.first }.min()
        val minY = list.flatMap { it.points }.map { it.second }.min()
        val maxY = list.flatMap { it.points }.map { it.second }.max()
        val height = maxY + 1
        val arr = Array(height) { Array(9) { '.' } }

        (0 until height).forEach { arr[it][0] = '|'; arr[it][8] = '|' }
        arr[0].indices.forEach { arr[0][it] = '_' }
        arr[0][0] = '+'
        arr[0][8] = '+'
//        list.flatMap { it.points }.map { Pair(it.first - minX, it.second - minY) }.forEach { arr[it.second][it.first] = '#' }
        list.flatMap { it.points }.forEach { arr[it.second][it.first] = '#' }
        arr.reverse()
        arr.forEach { row -> println(row.joinToString("")) }
        kotlin.io.println("")
    }

    data class Shape(val points: Set<Pair<Int, Int>>) {
        fun leftMost() = points.minBy { it.first }
        fun bottom() = points.minBy { it.second }

        fun colidesWith(other: Shape) = this.points.intersect(other.points).isNotEmpty()

        fun moveRight(amount: Int = 1): Shape = Shape(points.map { it.copy(first = it.first + amount) }.toSet())
        fun moveLeft(amount: Int = 1): Shape = moveRight(-amount)
        fun moveDown(amount: Int = 1): Shape = Shape(points.map { it.copy(second = it.second - amount) }.toSet())
        fun moveUp(amount: Int = 1): Shape = moveDown(-amount)

        fun isValid(otherShapes: List<Shape>) =
            points.all { it.first > leftWall && it.first < rightWall && it.second > floor } && otherShapes.none {
                it.colidesWith(this)
            }

        fun spawn(otherShapes: List<Shape>): Shape {
            val leftDiff = 3 - leftMost().first
            val highest = otherShapes.flatMap { it.points.map { it.second } }.maxOrNull() ?: floor
            val bot = bottom().second
            return moveRight(leftDiff).moveUp(highest - bot + 4)
        }
    }

    override fun sample(): String = ">>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>"

    fun apply(c: Char, shape: Shape, allShapes: List<Shape>): Pair<Shape, Boolean> {
        val nextShape = when (c) {
            '>' -> shape.moveRight()
            '<' -> shape.moveLeft()
            else -> TODO()
        }
        val s = nextShape.takeIf { it.isValid(allShapes) } ?: shape
        val ss = s.moveDown()
//        printShapes(allShapes + ss)
        return if (ss.isValid(allShapes)) {
            Pair(ss, true)
        } else Pair(s, false)
    }

    override fun solve(input: List<String>): Any {
        val pattern = infiniteSequenceOf(input.first().toList()).iterator()
        val shapes = allShapes().take(2022).iterator()
        var allShapes = emptyList<Shape>()
        while (shapes.hasNext()) {
            allShapes += moveShape(shapes.next().spawn(allShapes), pattern, allShapes)
        }
        return allShapes.flatMap { it.points }.map { it.second }.max()
    }

    private fun moveShape(next: Shape, pattern: Iterator<Char>, allShapes: List<Shape>): Shape {
        var s = apply(pattern.next(), next, allShapes)
        while (s.second) {
            s = apply(pattern.next(), s.first, allShapes)
        }
//        printShapes(allShapes + s.first)
        return s.first
    }

}


fun main() {
//    var shape = D17.shapeMinus.spawn(emptyList())
//    D17.printShapes(listOf(shape))
//    println()
//    shape = shape.moveRight().moveDown()
//    D17.printShapes(listOf(shape))
//    shape = shape.moveDown()
//    D17.printShapes(listOf(shape))
//
//    shape = shape.moveDown().moveLeft()
//    val list = listOf(shape)
//    D17.printShapes(list)
//
//    var shape2 = D17.shapePlus.spawn(list)
//    D17.printShapes(list + shape2)
    D17.solveSample(3068)
    D17.solve("day17.txt").println()
}