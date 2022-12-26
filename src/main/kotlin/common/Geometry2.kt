package common

data class P2(val x: Int, val y: Int) {
    operator fun plus(other: P2) = P2(this.x + other.x, this.y + other.y)
    operator fun plus(direction: Direction) = P2(this.x + direction.x, this.y + direction.y)
}

data class Direction(val x: Int, val y: Int)  {
    operator fun plus(other: Direction) = Direction(this.x + other.x, this.y + other.y)
}

object Directions {

    val east = Direction(1, 0)
    val south = Direction(0, 1)
    val west = Direction(-1, 0)
    val north = Direction(0, -1)

    val ne = north + east
    val nw = north + west
    val se = south + east
    val sw = south + west

    val cross = listOf(east, south, west, north)

    val allAdjacent = listOf(east, se, south, sw, west, nw, north, ne)

}
