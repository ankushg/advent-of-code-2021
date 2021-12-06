package day03

import AdventOfCodeDaySolution

fun main() {
    Day03.main()
}

object Day03 : AdventOfCodeDaySolution<Int>(
    dayNumber = "03",
    part1ExpectedAnswer = 198,
    part2ExpectedAnswer = 230
) {
    override fun part1(input: List<String>): Int {
        val mostCommonPerPosition = calculateMostCommonPerPosition(input)

        val gamma = mostCommonPerPosition.toDecimalInteger()
        val epsilon = mostCommonPerPosition.negated().toDecimalInteger()

        return gamma * epsilon
    }

    override fun part2(input: List<String>): Int {
        val oxygen_rating = input.filterIterativelyUntilLast { workingList, position ->
            val oneOccurrencePerPosition = calculateOneOccurencePerPosition(workingList)

            val desiredCharAtPosition = oneOccurrencePerPosition.desiredOxygenCharAtPosition(
                position = position,
                totalItems = workingList.size
            )

            ({ item ->
                item[position] == desiredCharAtPosition
            })
        }!!.toIntFromBinary()

        val co2_rating = input.filterIterativelyUntilLast { workingList, position ->
            val oneOccurrencePerPosition = calculateOneOccurencePerPosition(workingList)

            val desiredCharAtPosition = oneOccurrencePerPosition.desiredCO2CharAtPosition(
                position = position,
                totalItems = workingList.size
            )

            ({ item ->
                item[position] == desiredCharAtPosition
            })
        }!!.toIntFromBinary()

        return oxygen_rating * co2_rating
    }

    private fun calculateOneOccurencePerPosition(input: List<String>): IntArray {
        val digitCount = input.first().length

        val occurrenceMap = IntArray(size = digitCount)

        input.forEach { line ->
            assert(line.length == digitCount)
            line.forEachIndexed { index, char ->
                if (char == '1') {
                    occurrenceMap[index] = occurrenceMap[index] + 1
                }
            }
        }

        return occurrenceMap;
    }

    private fun calculateMostCommonPerPosition(input: List<String>): BooleanArray {
        val totalItems = input.size
        val digitCount = input.first().length

        val occurrenceMap = calculateOneOccurencePerPosition(input)

        return BooleanArray(size = digitCount) { position ->
            // TODO: how to break ties?
            occurrenceMap[position] > (totalItems / 2.0)
        }
    }

    private fun BooleanArray.toDecimalInteger(): Int {
        val binaryString = this.joinToString(separator = "") { if (it) "1" else "0" }
       return binaryString.toIntFromBinary()
    }

    private fun String.toIntFromBinary() : Int{
        return Integer.parseUnsignedInt(this, 2);
    }

    private fun BooleanArray.negated() = this.map { !it }.toBooleanArray()

    private fun IntArray.desiredOxygenCharAtPosition(position: Int, totalItems: Int): Char {
        val oneCountAtPosition = this[position]
        return when {
            oneCountAtPosition*2 < totalItems -> {
                // 1 is least frequent
                '0'
            }
            oneCountAtPosition*2 > totalItems -> {
                // 1 is most frequent
                '1'
            }
            else -> {
                // tiebreaker
                '1'
            }
        }
    }

    private fun IntArray.desiredCO2CharAtPosition(position: Int, totalItems: Int): Char {
        val oneCountAtPosition = this[position]
        return when {
            oneCountAtPosition*2 < totalItems -> {
                // 1 is least frequent
                '1'
            }
            oneCountAtPosition*2 > totalItems -> {
                // 1 is most frequent
                '0'
            }
            else -> {
                // tiebreaker
                '0'
            }
        }
    }

    private fun List<String>.filterIterativelyUntilLast(predicateGenerator: (List<String>, Int) -> ((String) -> Boolean)): String? {
        var workingList = this
        var iteration = 0

        while(workingList.size > 1) {
            workingList = workingList.filter(predicateGenerator(workingList, iteration))
            iteration++
        }

        return workingList.singleOrNull()
    }
}
