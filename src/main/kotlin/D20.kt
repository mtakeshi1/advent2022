package main.kotlin

object D20 : Solver {

    override fun sample(): String = "1\n" +
            "2\n" +
            "-3\n" +
            "3\n" +
            "-2\n" +
            "0\n" +
            "4"


    val sentinel = ListNode(Int.MIN_VALUE)

    data class ListNode(val num: Int) {
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
//            if(n == 0) this else if(n < 0) this.previous.walk(n + 1) else this.next.walk(n - 1)
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
//                this.moveAfter(prev.walk(this.num))
                this.insertAfter(prev.walk(this.num))
            } else if(this.num > 0){
                val next = this.next
                val prev = this.previous
                this.dettach()
                if(!next.isCoherent() || !prev.isCoherent()) throw RuntimeException()
                this.insertAfter(next.walk(this.num - 1))
//                this.moveAfter(this.walk(this.num))
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

    override fun solve(input: List<String>): Any {
        val listNode = input.map { ListNode(it.toInt()) }
        listNode.windowed(2).forEach { (a, b) -> a.next = b; b.previous = a }
        listNode.first().previous = listNode.last()
        listNode.last().next = listNode.first()
        listNode.forEach {node ->
//            println("swapping ${it.num}")
            node.swap()
//            if(!listNode.all { it.isCoherent() }) throw RuntimeException("bug")
        }
        if(!listNode.all { it.isCoherent() }) throw RuntimeException("bug")

        val node0 = listNode.find { it.num == 0 }!!
//        node0.printMe()
        val node1 = node0.walk(1000)
        val node2 = node0.walk(2000)
        val node3 = node0.walk(3000)
        return node1.num + node2.num + node3.num
    }

}


fun main() {

//    D20.solveSample(3)
    if(D20.solve("day20.txt") != 8764) throw RuntimeException()

//    D20.solve(listOf("5", "0", "1"))
}