package day09

import AdventOfCodeDaySolution
import com.github.ajalt.mordant.table.grid

fun main() {
    Day09.main()
}

typealias Point = Pair<Int, Int>

object Day09 : AdventOfCodeDaySolution<Int>(
    dayNumber = "09",
    part1ExpectedAnswer = 15,
    part2ExpectedAnswer = 1134
) {
    override fun part1(input: List<String>): Int {
        val grid = Grid(input.map {
            it.toList().map(Character::getNumericValue)
        })
        return grid.getLowPoints().sumOf { grid[it] + 1 }
    }

    override fun part2(input: List<String>): Int {
        val grid = Grid(input.map {
            it.toList().map(Character::getNumericValue)
        })
        val basins = grid.getBasins()
        return basins.values.map { it.size }.sortedDescending().take(3)
            .reduce { acc, value -> acc * value }
    }

    @JvmInline
    value class Grid(private val gridData: List<List<Int>>) {

        operator fun get(point: Point): Int {
            val (row, col) = point
            return gridData[row][col]
        }

        fun getLowPoints(): List<Point> {
            val list = mutableListOf<Point>()
            for (row in gridData.indices) {
                for (col in gridData[row].indices) {
                    val cellValue = this[row to col]
                    val neighborValues = getNeighborsOf(row to col).map(this::get)
                    if (neighborValues.all { it > cellValue }) {
                        list += row to col
                    }
                }
            }
            return list
        }

        fun getBasins(): Map<Point, Set<Point>> {
            return getLowPoints()
                .associateWith(::getBasin)
        }

        private fun getBasin(point: Point): Set<Point> {
            val basin = mutableSetOf<Point>(point)
            val visited = mutableSetOf<Point>()
            val toVisit = ArrayDeque(getNeighborsOf(point))

            while (toVisit.isNotEmpty()) {
                val currentPoint = toVisit.removeFirst()
                visited += currentPoint
                if (this[currentPoint] != 9) {
                    basin += currentPoint
                    toVisit += getNeighborsOf(currentPoint).filter { it !in visited }
                }
            }

            return basin
        }

        private fun getNeighborsOf(point: Point): List<Point> {
            val (row, col) = point
            val neighbors = mutableListOf<Pair<Int, Int>>()

            neighbors += row - 1 to col
            neighbors += row + 1 to col
            neighbors += row to col - 1
            neighbors += row to col + 1

            return neighbors.filter { (row, col) ->
                !(row < 0 || row >= gridData.size || col < 0 || col >= gridData.first().size)
            }
        }
    }
}
