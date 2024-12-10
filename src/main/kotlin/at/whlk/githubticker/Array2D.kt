package at.whlk.githubticker

import java.time.LocalDate

class Array2D<T>(val xSize: Int, val ySize: Int, val array: Array<Array<T>>) {

    companion object {

        inline operator fun <reified T> invoke() = Array2D(0, 0, Array(0) { emptyArray<T>() })

        inline operator fun <reified T> invoke(xWidth: Int, yWidth: Int) =
                Array2D(xWidth, yWidth, Array(xWidth) { arrayOfNulls<T>(yWidth) })

        inline operator fun <reified T> invoke(xWidth: Int, yWidth: Int, operator: (Int, Int) -> (T)): Array2D<T> {
            val array = Array(xWidth) {
                Array(yWidth) { operator(it, it) }
            }
            return Array2D(xWidth, yWidth, array)
        }
    }

    operator fun get(x: Int, y: Int): T {
        return array[x][y]
    }

    operator fun set(x: Int, y: Int, t: T) {
        array[x][y] = t
    }

    inline fun forEachIndexed(operation: (x: Int, y: Int, T) -> Unit) {
        array.forEachIndexed { x, p -> p.forEachIndexed { y, t -> operation.invoke(x, y, t) } }
    }

    fun print() {
        for (y in 0 until ySize) {
            for (x in 0 until xSize) {
                print(get(x, y).ixify())
            }
            println()
        }

    }

    private fun <T> T?.ixify(): Char {
        return when (val char = this) {
            is Boolean -> if (char) '#' else '.'
            is LocalDate -> char.dayOfWeek.toString()[0]
            null -> ' '
            else -> this.toString()[0]
        }
    }
}
