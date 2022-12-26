package y2022

import common.Solver
import java.math.BigDecimal

object D21 : Solver {

    override fun sample(): String = """
        root: pppw + sjmn
        dbpl: 5
        cczh: sllz + lgvd
        zczc: 2
        ptdq: humn - dvpt
        dvpt: 3
        lfqf: 4
        humn: 5
        ljgn: 2
        sjmn: drzm * dbpl
        sllz: 4
        pppw: cczh / lfqf
        lgvd: ljgn * ptdq
        drzm: hmdt - zczc
        hmdt: 32
    """.trimIndent()

    fun interface DoubleOp {
        fun apply(a: Double, b: Double): Double
    }

    interface Exp {
        val value: Double
        fun isDefined(): Boolean
        fun solveFor(target: Double): Double
    }

    data class Lit(override val value: Double) : Exp {
        override fun isDefined(): Boolean = true
        override fun toString(): String = this.value.toString()
        override fun solveFor(target: Double): Double = TODO()

    }

    fun format(d: Double) = BigDecimal.valueOf(d).toPlainString()

    data class Op(val left: String, val right: String, val op: DoubleOp, val opString: String, val context: Map<String, Exp>) : Exp {

        override val value: Double by lazy { op.apply(context[left]!!.value, context[right]!!.value) }
        override fun toString(): String {

            return if (isDefined()) {
                format(value)
            } else {
                val leftS = context[left]?.toString() ?: left
                val rightS = context[right]?.toString() ?: right
                "($leftS $opString $rightS)";
            }
        }

        override fun isDefined(): Boolean {
            if (left.startsWith(targetMe) || right.startsWith(targetMe)) return false
            return context[left]!!.isDefined() && context[right]!!.isDefined()
        }

        override fun solveFor(target: Double): Double {
            if (isDefined()) return value
            println(this.toString() + " = " + format(target))
            if (context[left] != null && context[left]!!.isDefined()) {
                val k = context[left]!!.value
                val x = context[right]!!
                return when (opString) {
                    "+" -> x.solveFor(target - k)
                    "-" -> x.solveFor(k - target)
                    "*" -> x.solveFor(target / k)
                    "/" -> x.solveFor(k / target)
                    else -> TODO()
                }

                // k op X = target
                // k + X = target -> X = target - k
                // k - X = target -> X = k - target
                // k * X = target -> X = target / k
                // k / X = target -> X = k / target
            } else if (context[right]!!.isDefined()) {
                val k = context[right]!!.value
                val x = context[left]!!
                // X op k = target
                // X + k = target -> X = target - k
                // X - k = target -> X = target + k
                // X * k = target -> X = target / k
                // X / k = target -> X = target * k
                return when (opString) {
                    "+" -> x.solveFor(target - k)
                    "*" -> x.solveFor(target / k)
                    "-" -> x.solveFor(k + target)
                    "/" -> x.solveFor(k * target)
                    else -> TODO()
                }
            }
            TODO("Not yet implemented")
        }

    }

    fun parseOne(line: String, context: MutableMap<String, Exp>) {
        val (name, v) = line.split(":")
        if (v.trim().matches(Regex("-?[0-9]+"))) {
            context[name] = Lit(v.trim().toDouble())
        } else {
            val (left, op, right) = v.trim().split(" ")
            val theOp = parse(op)
            context[name] = Op(left, right, theOp, op, context)
        }
    }

    private fun parse(op: String): DoubleOp {
        val theOp = when (op) {
            "+" -> DoubleOp { a, b -> a + b }
            "-" -> DoubleOp { a, b -> a - b }
            "*" -> DoubleOp { a, b -> a * b }
            "/" -> DoubleOp { a, b -> a / b }
            else -> TODO()
        }
        return theOp
    }

    private fun parseReverse(op: String): DoubleOp {
        val theOp = when (op) {
            "+" -> DoubleOp { a, b -> a - b }
            "-" -> DoubleOp { a, b -> a + b }
            "*" -> DoubleOp { a, b -> a / b }
            "/" -> DoubleOp { a, b -> a * b }
            else -> TODO()
        }
        return theOp
    }

    val targetMe = "humn"

    override fun solve(input: List<String>): Any {
        val context = mutableMapOf<String, Exp>()
        input.forEach { parseOne(it, context) }
        return context["root"]!!.value
    }

    override fun solveb(input: List<String>): Any {
        val context = mutableMapOf<String, Exp>()
        input.filter { !it.startsWith(targetMe) }.forEach { parseOne(it, context) }
        val exp = context["root"]!!
        if (exp is Op) {
            if(context[exp.left]!!.isDefined()) {
                val target = context[exp.left]!!.value
                context[exp.right]!!.solveFor(target)
            } else if (context[exp.right]!!.isDefined()) {
                val target = context[exp.right]!!.value
                context[exp.left]!!.solveFor(target)

            }
//            println(context[exp.left].toString())
//            println(exp.opString)
//            println(context[exp.right].toString())
        }
        return exp.solveFor(0.0)
    }

}

fun main() {
//    D21.solveSample(152L)
//    D21.solve("day21.txt")


//    D21.solveSampleB()
    D21.solveb("day21.txt")
}