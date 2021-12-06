package day04

import AdventOfCodeDaySolution
import kotlin.properties.Delegates

fun main() {
    Day04.main()
}

object Day04 : AdventOfCodeDaySolution<Int>(
    dayNumber = "04",
    part1ExpectedAnswer = 4512,
    part2ExpectedAnswer = 1924
) {
    override fun part1(input: List<String>): Int {
        val bingoState = callNumbersUntil(input) {
            it.getCompletedBoards().isNotEmpty()
        }

        return bingoState.getCompletedBoards().maxOf { bingoState.getScore(it) }
    }

    override fun part2(input: List<String>): Int {
        val bingoState = callNumbersUntil(input) {
            it.previousIncompleteBoards.size == 1 && it.incompleteBoards.size == 0
        }

        return bingoState.getScore(bingoState.previousIncompleteBoards.single())
    }

    private fun callNumbersUntil(
        input: List<String>,
        predicate: (BingoState) -> Boolean
    ): BingoState {
        val numbersToCall = input[0].split(",").map { Integer.parseInt(it) }
        val boards = input.drop(1).chunked(6) {
            val boardContent = it.drop(1)
            BingoBoard.parse(boardContent)
        }
        val bingoState = BingoState(boards)

        var numberIndex = 0
        while (!predicate.invoke(bingoState) && numberIndex < numbersToCall.size) {
            val numberToCall = numbersToCall[numberIndex]
            bingoState.callNumber(numberToCall)
            numberIndex++
        }

        return bingoState
    }
}

class BingoState(
    private val boards: List<BingoBoard>
) {
    private val _incompleteBoards = boards.toMutableSet()
    private val calledNumbers = mutableSetOf<Int>()
    private var lastCalledNumber by Delegates.notNull<Int>()

    val incompleteBoards: Set<BingoBoard> by this::_incompleteBoards
    var previousIncompleteBoards = incompleteBoards.toSet()

    fun callNumber(number: Int) {
        calledNumbers += number
        lastCalledNumber = number
        previousIncompleteBoards = incompleteBoards.toSet()
        _incompleteBoards -= getCompletedBoards().toSet()
    }

    fun getCompletedBoards(): Collection<BingoBoard> {
        return boards
            .filter { it.isComplete(calledNumbers) }
    }

    fun getScore(board: BingoBoard): Int {
        return lastCalledNumber * board.computeSubScore(calledNumbers)
    }
}

data class BingoBoard private constructor(
    private val contents: List<List<Int>>
) {
    companion object {
        fun parse(boardContents: List<String>): BingoBoard {
            val contents = boardContents.map { rowString ->
                rowString
                    .split("\\s+".toRegex())
                    .filter { it.isNotBlank() }
                    .map { Integer.parseInt(it) }
            }

            return BingoBoard(contents)
        }
    }

    fun isComplete(calledNumbers: Set<Int>): Boolean {
        return isAnyRowComplete(calledNumbers) || isAnyColumnComplete(calledNumbers)
    }

    private fun isAnyRowComplete(calledNumbers: Set<Int>): Boolean {
        return contents.any { row -> row.all { it in calledNumbers } }
    }

    private fun isAnyColumnComplete(calledNumbers: Set<Int>): Boolean {
        return contents.indices.any { colNum ->
            contents
                .map { row -> row[colNum] }
                .all { it in calledNumbers }
        }
    }

    fun computeSubScore(calledNumbers: Set<Int>): Int {
        return contents.sumOf { row ->
            row.filter { it !in calledNumbers }
                .sum()
        }
    }
}