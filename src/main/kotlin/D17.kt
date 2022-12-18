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
    val allShapeList = listOf(shapeMinus, shapePlus, shapeL, shapeI, shapeSq)

    fun allShapes(): Sequence<Shape> {

        return infiniteSequenceOf(allShapeList)
    }

    fun printShapes(list: List<Shape>) {
        if (list.isEmpty()) return
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
        var accShape = Shape(emptySet())
        var accHeight = 0L
        while (shapes.hasNext()) {
            val existing = listOf(accShape)
            val next = moveShape(shapes.next().spawn(existing), pattern, existing)
            val (yToDelete, newShape) = simplify(Shape(accShape.points + next.points))
            accShape = newShape
            accHeight += yToDelete
//            allShapes += moveShape(shapes.next().spawn(allShapes), pattern, allShapes)
        }
//        return allShapes.flatMap { it.points }.map { it.second }.max()
//        TODO()
        return accShape.points.map { it.second }.max() + accHeight
    }

    private fun moveShape(next: Shape, pattern: Iterator<Char>, allShapes: List<Shape>): Shape {
        var s = apply(pattern.next(), next, allShapes)
        while (s.second) {
            s = apply(pattern.next(), s.first, allShapes)
        }
//        printShapes(allShapes + s.first)
        return s.first
    }

    private fun moveShapeB(next: Shape, pattern: Iterator<Char>, allShapes: List<Shape>): Shape {
        var s = apply(pattern.next(), next, allShapes)
        while (s.second) {
            s = apply(pattern.next(), s.first, allShapes)
        }
//        printShapes(allShapes + s.first)
        return s.first
    }

    override fun solveb(input: List<String>): Any {
        val pattern = input.first()
        val patternSequence = wrappingSequence(pattern.length).iterator()
        val shapeIndexSequence = wrappingSequence(allShapeList.size).iterator()
        var accShape = Shape(emptySet())
        var accHeight = 0L
        val map: MutableMap<Triple<Shape, Int, Int>, Pair<Long, Long>> = hashMapOf()

        fun moveShapeB(next: Shape, allShapes: List<Shape>): Pair<Shape, Int> {
            var wind = patternSequence.next()
            var s = apply(pattern[wind], next, allShapes)
            while (s.second) {
                wind = patternSequence.next()
                s = apply(pattern[wind], s.first, allShapes)
            }
            return Pair(s.first, wind)
        }
        map[Triple(accShape, 0, 0)] = Pair(0L, 0L)
        var its = 0L
        var same = 0
        var firstRepeat: Pair<Triple<Shape, Int, Int>, Pair<Long, Long>> = Pair(Triple(accShape, 0, 0), Pair(0, 0))
        while (same < 5000) {
            val shapeIndex = shapeIndexSequence.next();
            val existing = listOf(accShape)
            val (next, wind) = moveShapeB(allShapeList[shapeIndex].spawn(existing), existing)
            val (deleted, newShape) = simplify(Shape(accShape.points + next.points))
            accShape = newShape
            accHeight += deleted
            val key= Triple(accShape, shapeIndex, wind)
            val prev = map[key]
            if(prev != null) {
                println("previous iteration ${map[key]}, current iteration $its, shapeIndex: $shapeIndex, wind: $wind, points: ${accShape.points.size}, prev ${prev.second}, current: $accHeight")

                firstRepeat = key to Pair(its, accHeight)
                break
//                same++
            } else {
                map[key] = Pair(its, accHeight)
            }
            its++
        }
        val max = 1000000000000L
        val firstPattern = map[firstRepeat.first]!!
        val loopLength = firstRepeat.second.first - firstPattern.first
        val loops = (max - firstPattern.first)/loopLength
        val remaning = firstPattern.first + (max - firstPattern.first)%loopLength
        val heightPerLoop = firstRepeat.second.second - firstPattern.second
        val finalPattern = map.entries.find { it.value.first == remaning }!!.key.first
        return finalPattern.points.maxOf { it.second } + firstPattern.second + (loops * heightPerLoop)

    }

    private fun simplify(shape: Shape): Pair<Int, Shape> {
        val maxY = shape.points.map { it.second }.max()
        val heightToDelete =
            (maxY downTo 0).find { y -> ((leftWall + 1) until rightWall).all { shape.points.contains(Pair(it, y)) } }
        return heightToDelete?.let {
                line -> val nextShape =
            Shape(shape.points.filter { it.second > line }.map { Pair(it.first, it.second - line) }.toSet())
            Pair(line, nextShape) }
            ?: Pair(0, shape)
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
//    D17.solveSample(3068L)

    //previous iteration (350, 551), current iteration 2065, shapeIndex: 0, wind: 2050, points: 3, prev 551, current: 3125
    //previous iteration (2665, 4023), current iteration 4380, shapeIndex: 0, wind: 5595, points: 3, prev 4023, current: 6597

    //previous iteration (351, 551), current iteration 2066, shapeIndex: 1, wind: 2054, points: 8, prev 551, current: 3125
    //previous iteration (2666, 4023), current iteration 4381, shapeIndex: 1, wind: 5600, points: 8, prev 4023, current: 6597
//    D17.solveb("day17.txt").println() // 10091
    D17.solveSampleB().println()
}