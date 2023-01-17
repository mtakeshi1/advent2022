package advent2020

object D23 : Solver2020 {

    fun round(cupOrder: List<Int>): List<Int> {
        val pickup = cupOrder.drop(1).take(3)
        val nextOrder = listOf(cupOrder.first()) + cupOrder.drop(4)
        var destinationCup = cupOrder.first() - 1
        while (!nextOrder.contains(destinationCup)) {
            if (destinationCup <= 0) destinationCup = 9
            else destinationCup--
        }
        val destinationIndex = nextOrder.indexOf(destinationCup) + 1
        val next = nextOrder.take(destinationIndex) + pickup + nextOrder.drop(destinationIndex)
        return next.drop(1) + next.first()
    }

    data class Cup(val cupValue: Int, var next: Cup? = null) {
        fun unlink(amount: Int): Cup {
            check(amount > 0)
            val toRemove = this.next!!
            var end = toRemove
            repeat(amount-1) {
                end = end.next!!
            }
            this.next = end.next
            end.next = null
            return toRemove
        }

        fun contains(nextCup: Int): Boolean = nodeContains(this, nextCup)

        fun link(pickup: Cup) {
            var last = pickup
            while (last.next != null) last = last.next!!
            last.next = this.next
            this.next = pickup
        }

        fun take(amount: Int): List<Int> {
            if(amount <= 0) return emptyList()
            return listOf(this.cupValue) + this.next!!.take(amount-1)
        }

        override fun toString(): String {
            return this.take(9).joinToString(" ")
        }

        companion object {
            tailrec fun nodeContains(cup: Cup, v: Int): Boolean {
                if(cup.cupValue == v) return true
                val n = cup.next
                return if(n == null) false else nodeContains(n, v)
            }

            fun fromList(list: List<Int>): List<Cup> {
                val cups = list.map { Cup(it) }
                cups.zipWithNext().forEach { (prev, next) -> prev.next = next }
                cups.last().next =  cups.first()
                return cups
            }

        }

    }

    fun llistRound(currentCup: Cup, cupByValue: Map<Int, Cup>, maxCup: Cup) {
        val pickup = currentCup.unlink(3)
        var nextCupValue = currentCup.cupValue - 1
        if(nextCupValue == 0) nextCupValue = maxCup.cupValue
        while (pickup.contains(nextCupValue)) {
            if(--nextCupValue == 0)nextCupValue = maxCup.cupValue
        }
        val dropOff = cupByValue[nextCupValue]!!
        dropOff.link(pickup)
//        println(currentCup)
    }


    override fun solve(input: List<String>): Any {
//        val i = input.first().map { it - '0' }
//        val r = (0 until 100).fold(i) { a, iter ->
//            println("iteration $iter : $a")
//            round(a)
//        }
////        return r.joinToString(separator = "")
//        return (r + r).dropWhile { it != 1 }.take(9).drop(1).joinToString(separator = "")
        val cups = Cup.fromList(input.first().map { it - '0' })
        val cupIndexes = cups.map { t -> t.cupValue to t }.toMap()
        val maxCup = cups.maxBy { it.cupValue }
        var currentCup = cups.first()
        repeat(100) {
            llistRound(currentCup, cupIndexes, maxCup)
            currentCup = currentCup.next!!
        }
        return cupIndexes[1]!!.take(9).drop(1).joinToString (separator = "")
    }

    override fun solveb(input: List<String>): Any {
        val i = input.first().map { it - '0' } + (10..1000_000)
//        val r = (0 until 10_000_000).fold(i) { a, c ->
//            if((c % 1000) == 0) println(a)
//            round(a)
//        }
////        return r.joinToString(separator = "")
//        return (r + r).asSequence().dropWhile { it != 1 }.take(3).drop(1).map { it.toLong() }.reduce{ a, b -> a * b }
        val cups = Cup.fromList(i)
        val cupIndexes = cups.associateBy { t -> t.cupValue }
        val maxCup = cups.maxBy { it.cupValue }
        var currentCup = cups.first()
        repeat(10000000) {
            llistRound(currentCup, cupIndexes, maxCup)
            currentCup = currentCup.next!!
        }
        return cupIndexes[1]!!.take(3).map { it.toLong() }.reduce{ a, b -> a * b}
    }


}

fun main() {
    println(D23.solve("389125467") == "67384529")
    println(D23.solve("925176834"))
    println(D23.solveb("925176834"))

    //91408386135
    //1214072919
}
