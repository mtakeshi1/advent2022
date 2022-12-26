package y2022

import common.Solver

object D20 : Solver {

    override fun sample(): String = "1\n" +
            "2\n" +
            "-3\n" +
            "3\n" +
            "-2\n" +
            "0\n" +
            "4"


    val sentinel = ListNode(Long.MIN_VALUE)

    data class ListNode(val num: Long) {
        var previous: ListNode = sentinel
        var next: ListNode = sentinel

        fun dettach() {
            this.previous.next = this.next
            this.next.previous = this.previous
            this.previous = sentinel
            this.next = sentinel
        }

        fun insertAfter(other: ListNode) {
            this.next = other.next
            this.previous = other

            this.next.previous = this
            other.next = this
        }

        fun moveAfter(other: ListNode) {
            if(this == other) return
            // this -> other -> A
            // other -> this -> A
            // removed myself
            println("moving ${this.num} between ${other.num} and ${other.next.num}")
            this.previous.next = this.next
            this.next.previous = this.previous

            //fix myself
            this.previous = other
            this.next = other.next

            // fix other neighboors
            other.next.previous = this
            other.next = this

//            printMe()
        }

        fun walk(n: Int): ListNode {
            var node = this
            if (n > 0) repeat(n) { node = node.next }
            else repeat(-n) { node = node.previous }
            return node
        }

        fun swap() {
            if(!isCoherent() || !this.previous.isCoherent() || !this.next.isCoherent()) {
                throw RuntimeException("fuuuu")
            }

            if(this.num < 0) {
                val next = this.next
                val prev = this.previous
                this.dettach()
                if(!next.isCoherent() || !prev.isCoherent()) throw RuntimeException()
                this.insertAfter(prev.walk(this.num.toInt()))
            } else if(this.num > 0){
                val next = this.next
                val prev = this.previous
                this.dettach()
                if(!next.isCoherent() || !prev.isCoherent()) throw RuntimeException()
                this.insertAfter(next.walk((this.num - 1).toInt()))
            }
            if(!isCoherent() || !this.previous.isCoherent() || !this.next.isCoherent()) {
                throw RuntimeException("fuuuu")
            }
        }

        fun swapB(max: Int) {
            if(!isCoherent() || !this.previous.isCoherent() || !this.next.isCoherent()) {
                throw RuntimeException("fuuuu")
            }

            if(this.num < 0) {
                val next = this.next
                val prev = this.previous
                this.dettach()
                if(!next.isCoherent() || !prev.isCoherent()) throw RuntimeException()
                this.insertAfter(prev.walk(this.num.mod(max - 1)))
            } else if(this.num > 0){
                val next = this.next
                val prev = this.previous
                this.dettach()
                if(!next.isCoherent() || !prev.isCoherent()) throw RuntimeException()
                this.insertAfter(next.walk((this.num - 1).mod(max - 1)))
            }
            if(!isCoherent() || !this.previous.isCoherent() || !this.next.isCoherent()) {
                throw RuntimeException("fuuuu")
            }
        }


        fun printMe() {
            var n = this
            do {
                print("${n.num} ")
                n = n.next
            } while (n != this)
            println()
        }

        fun isCoherent() : Boolean {
            val b = this.previous.next == this && this.next.previous == this
            return b
        }

    }

    val decryptionKey = 811589153L
    override fun solve(input: List<String>): Any {
        val listNode = input.map { ListNode(it.toLong()) }
        listNode.windowed(2).forEach { (a, b) -> a.next = b; b.previous = a }
        listNode.first().previous = listNode.last()
        listNode.last().next = listNode.first()
        listNode.forEach {node ->
//            println("swapping ${it.num}")
            node.swap()
//            if(!listNode.all { it.isCoherent() }) throw RuntimeException("bug")
        }
        if(!listNode.all { it.isCoherent() }) throw RuntimeException("bug")

        val node0 = listNode.find { it.num == 0L }!!
//        node0.printMe()
        val node1 = node0.walk(1000)
        val node2 = node0.walk(2000)
        val node3 = node0.walk(3000)
        return node1.num + node2.num + node3.num
    }

    override fun solveb(input: List<String>): Any {
        val listNode = input.map { ListNode(it.toLong() * decryptionKey) }
        listNode.windowed(2).forEach { (a, b) -> a.next = b; b.previous = a }
        listNode.first().previous = listNode.last()
        listNode.last().next = listNode.first()
        repeat(10) {
            listNode.forEach {node ->
//            println("swapping ${it.num}")
//                node.swap()
                node.swapB(listNode.size)
//            if(!listNode.all { it.isCoherent() }) throw RuntimeException("bug")
            }
        }
        if(!listNode.all { it.isCoherent() }) throw RuntimeException("bug")

        val node0 = listNode.find { it.num == 0L }!!
//        node0.printMe()
        val node1 = node0.walk(1000)
        val node2 = node0.walk(2000)
        val node3 = node0.walk(3000)
        return node1.num + node2.num + node3.num
    }

}


fun main() {

//    D20.solveSample(3L)
//    if(D20.solve("day20.txt") != 8764) throw RuntimeException()

    D20.solveSampleB(1623178306L)
    D20.solveb("day20.txt")
//    D20.solve(listOf("5", "0", "1"))
}