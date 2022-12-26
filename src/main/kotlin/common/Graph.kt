package common

import java.util.*

class Graph<N, K>(val nodeIdentifier: (N) -> K, val link: (N) -> Sequence<N>) {

    companion object {
        fun <N> fromIdentity(link: (N) -> Sequence<N>): Graph<N, N> = Graph({ a -> a }, link)
    }

    fun dfs(from: N, destination: (N) -> Boolean): List<N>? {
        TODO()
    }

    fun bfs(from: N, destination: (N) -> Boolean): List<N>? {
        TODO()
    }

    data class Pred<N>(val pred: Pred<N>?, val current: N) {
        fun collect(): List<N> {
            return if (pred == null) listOf(current)
            else pred.collect() + current
        }

    }

    fun <J : Comparable<J>> aStar(from: N, destination: (N) -> Boolean, filter: (N) -> Boolean, priority: (N) -> J): List<N>? {
        val cmp = Comparator.comparing<Pred<N>, J> { priority(it.current) }
        val queue = PriorityQueue(cmp)
        queue.add(Pred(null, from))
        val visited = mutableMapOf<K, N>()
        while (queue.isNotEmpty()) {
            val next = queue.poll()
            val current = next.current
            val k = nodeIdentifier(current)
            if (visited.put(k, current) != null) {
                continue
            }
            if (destination(current)) {
                return next.collect()
            }
            if (filter(current)) {
                link(current).forEach { n ->
                    if (filter(n) && !visited.contains(nodeIdentifier(n))) {
                        queue.add(Pred(next, n))
                    }
                }
            }
        }
        return null
    }

}
