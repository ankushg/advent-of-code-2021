package day05

import AdventOfCodeDaySolution

fun main() {
    Day05.main()
}

object Day05 : AdventOfCodeDaySolution<Int>(
    dayNumber = "05",
    part1ExpectedAnswer = 5,
    part2ExpectedAnswer = 12
) {
    override fun part1(input: List<String>): Int {
        val segments = input.map(LineSegment.Companion::parse)

        val (width, height) = getGridSizeForSegments(segments)
        val grid = Grid(width, height)

        segments.flatMap(LineSegment::getPointsOnLineExcludingDiagonals)
            .forEach(grid::markPoint)

        return grid.contents.sumOf { row -> row.count { it >= 2 } }
    }

    override fun part2(input: List<String>): Int {
        val segments = input.map(LineSegment.Companion::parse)

        val (width, height) = getGridSizeForSegments(segments)
        val grid = Grid(width, height)

        segments.flatMap(LineSegment::getPointsOnLine)
            .forEach(grid::markPoint)

        return grid.contents.sumOf { row -> row.count { it >= 2 } }
    }
}

@JvmInline
value class Point(val pair: Pair<Int, Int>) {
    companion object {
        fun parse(input: String): Point {
            val (x, y) = input.split(",").map { Integer.parseInt(it) }
            return Point(x to y)
        }
    }

    operator fun component1() = pair.component1()
    operator fun component2() = pair.component2()

    val x: Int get() = pair.first
    val y: Int get() = pair.second
}

fun getGridSizeForSegments(collection: Collection<LineSegment>): Pair<Int, Int> {
    val points = collection.flatMap { listOf(it.start, it.end) }
    val width = points.maxOf { it.x } + 1
    val height = points.maxOf { it.y } + 1

    return width to height
}

class Grid(
    width: Int,
    height: Int
) {
    val contents = Array(height) {
        IntArray(width)
    }

    fun markPoint(point: Point) {
        val (x, y) = point
        contents[y][x] = contents[y][x] + 1
    }

    override fun toString(): String {
        return contents.joinToString(separator = "\n") { row ->
            row.joinToString(separator = "") { count ->
                if (count == 0) "." else count.toString()
            }
        }
    }
}

data class LineSegment(
    val start: Point,
    val end: Point
) {
    companion object {
        fun parse(input: String): LineSegment {
            val (startString, endString) = input.split(" -> ")
            return LineSegment(
                Point.parse(startString),
                Point.parse(endString)
            )
        }
    }

    fun getPointsOnLineExcludingDiagonals(): List<Point> {
        return when (slope) {
            null -> { // Vertical
                (start.y toward end.y).map { Point(start.x to it) }
            }
            0.0 -> { // Horizontal
                (start.x toward end.x).map { Point(it to start.y) }
            }
            else -> {
                emptyList()
            }
        }
    }

    fun getPointsOnLine(): List<Point> {
        return when (val slope = slope) {
            null -> { // Vertical
                (start.y toward end.y).map {
                    Point(start.x to it)
                }.assertContainsEndpoints()
            }
            0.0 -> { // Horizontal
                (start.x toward end.x).map {
                    Point(it to start.y)
                }.assertContainsEndpoints()
            }
            1.0, -1.0 -> {
                (start.x toward end.x).map { newX ->
                    val newY = (slope * (newX - start.x)).toInt() + start.y
                    Point(newX to newY)
                }.assertContainsEndpoints()
            }
            else -> {
                emptyList()
            }
        }
    }

    fun List<Point>.assertContainsEndpoints(): List<Point> {
        check(start in this) {
            "Start ($start) not included in $this"
        }
        check(end in this) {
            "End ($end) not included in $this"
        }

        return this
    }

    private val slope: Double?
        get() {
            if (start.x == end.x) return null

            return (end.y.toDouble() - start.y) / (end.x.toDouble() - start.x)
        }

    /**
     * Helper function that lets you ignore direction when creating a range
     *
     * 1 toward 10 -> 1, 2, 3, 4...
     * 10 toward 1 -> 10, 9 , 8...
     */
    private infix fun Int.toward(to: Int): IntProgression {
        val step = if (this > to) -1 else 1
        return IntProgression.fromClosedRange(this, to, step)
    }
}