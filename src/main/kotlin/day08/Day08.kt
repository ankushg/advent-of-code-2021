package day08

import AdventOfCodeDaySolution
import java.lang.IllegalStateException

fun main() {
    Day08.main()
}

fun <K, V> Map<K, V>.reversed() = HashMap<V, K>().also { newMap ->
    entries.forEach { newMap[it.value] = it.key }
}


object Day08 : AdventOfCodeDaySolution<Int>(
    dayNumber = "08",
    part1ExpectedAnswer = 26,
    part2ExpectedAnswer = 61229
) {
    override fun part1(input: List<String>): Int {
        val outputs = unscrambleInputData(input)

        val desiredSet = setOf(1, 4, 7, 8)

        return outputs.sumOf { it ->
            it.count { it in desiredSet }
        }
    }

    override fun part2(input: List<String>): Int {
        val outputs = unscrambleInputData(input)

        return outputs.sumOf { it ->
            Integer.parseInt(it.joinToString(separator = "") { it.toString() })
        }
    }

    private fun unscrambleInputData(input: List<String>): List<List<Int>> {
        return input.map {
            val (signalPatternsString, outputValuesString) = it.split("|")
            val signalPatterns = signalPatternsString.trim().split(" ").map { str ->
                str.map(::ScrambledSegment).toSet()
            }
            val outputValues = outputValuesString.trim().split(" ").map { str ->
                str.map(::ScrambledSegment).toSet()
            }

            ScrambledInput(signalPatterns, outputValues)
                .unscramble()
        }
    }
}

val allSegments = setOf('a', 'b', 'c', 'd', 'e', 'f', 'g')

val numbersToSegments = mapOf(
    0 to "abcefg".map(::UnscrambledSegment).toSet(),
    1 to "cf".map(::UnscrambledSegment).toSet(),
    2 to "acdeg".map(::UnscrambledSegment).toSet(),
    3 to "acdfg".map(::UnscrambledSegment).toSet(),
    4 to "bcdf".map(::UnscrambledSegment).toSet(),
    5 to "abdfg".map(::UnscrambledSegment).toSet(),
    6 to "abdefg".map(::UnscrambledSegment).toSet(),
    7 to "acf".map(::UnscrambledSegment).toSet(),
    8 to "abcdefg".map(::UnscrambledSegment).toSet(),
    9 to "abcdfg".map(::UnscrambledSegment).toSet()
)

val segmentsToNumber = numbersToSegments.reversed()

@JvmInline
value class ScrambledSegment(val label: Char)

@JvmInline
value class UnscrambledSegment(val label: Char)

private class ScrambledInput(
    val scrambledSignals: List<Set<ScrambledSegment>>,
    val scrambledOutput: List<Set<ScrambledSegment>>
) {
    private fun Map<ScrambledSegment, MutableSet<UnscrambledSegment>>.setKnown(
        scrambledChar: ScrambledSegment,
        unscrambledChar: UnscrambledSegment
    ) {
        this.forEach { (key, candidates) ->
            if (key == scrambledChar) candidates.removeIf { it != unscrambledChar }
            else candidates.removeIf { it == unscrambledChar }
        }
    }

    fun unscramble(): List<Int> {
        // key: scrambled segment
        // value: potential unscrambled segments that we haven't eliminated yet
        val unscramblerMap: Map<ScrambledSegment, MutableSet<UnscrambledSegment>> =
            allSegments.associate {
                ScrambledSegment(it) to allSegments.map(::UnscrambledSegment).toMutableSet()
            }

        // populate the maps of Int -> scrambled strings as we encounter them
        val revealedScrambledMapping = mutableMapOf<Int, Set<ScrambledSegment>>()

        // populate obvious values based on length
        scrambledSignals.forEach { scrambledPattern ->
            val obviousValue = when (scrambledPattern.size) {
                2 -> 1
                3 -> 7
                4 -> 4
                7 -> 8
                else -> null
            }

            if (obviousValue != null) {
                revealedScrambledMapping[obviousValue] = scrambledPattern
            }
        }

        // remove everything we already know 
        revealedScrambledMapping.forEach { (obviousValue, scrambledPattern) ->
            val actualSegments = numbersToSegments[obviousValue]!!
            unscramblerMap.forEach { (c, workingValues) ->
                if (c in scrambledPattern) {
                    workingValues.removeIf { it !in actualSegments }
                } else {
                    workingValues.removeIf { it in actualSegments }
                }
            }
        }
        
        val scrambled6 =
            // of the six-letter codes (0, 6, 9)
            scrambledSignals.filter { it.size == 6 }
                // the code missing a char used in the pattern for 1 *must* be 6
                .single { !it.containsAll(revealedScrambledMapping[1]!!) }
        revealedScrambledMapping[6] = scrambled6

        // the segment from 1 missing from the code for 6 must map to segment c
        val scrambledC = revealedScrambledMapping[1]!!.single {
            it !in scrambled6.toSet()
        }
        unscramblerMap.setKnown(scrambledC, UnscrambledSegment('c'))


        // of the five-letter codes (2, 3, 5)
        val fiveLetterCodes = scrambledSignals.filter { it.size == 5 }.toSet()

        // we know which letter unscrambles to f by now: remove it
        val scrambledF = unscramblerMap.entries.single { (_, v) ->
            v == setOf(UnscrambledSegment('f'))
        }.key
        // the one that doesn't contain an scrambled f, is 2
        val scrambled2 = fiveLetterCodes.single { scrambledF !in it }
        revealedScrambledMapping[2] = scrambled2
        // the letter in 2 that is unused by 3 and 5 must unscramble to e
        val threeAndFive: Set<Set<ScrambledSegment>> = fiveLetterCodes.minusElement(scrambled2)
        val scrambledE = (scrambled2 - threeAndFive.flatten().toSet()).single()
        unscramblerMap.setKnown(scrambledE, UnscrambledSegment('e'))

        // of the five letter codes, b is the only one that occurs once and is still unknown
        val scrambledB = fiveLetterCodes.flatMap { it.toList() }.groupBy { it }
            .filterValues { it.size == 1 }.keys
            .single { unscramblerMap[it]?.size != 1 }
        unscramblerMap.setKnown(scrambledB, UnscrambledSegment('b'))


        val unscrambledPatterns = revealedScrambledMapping.reversed().toMutableMap()

        return scrambledOutput.map {
            unscrambledPatterns.computeIfAbsent(it) { key ->
                unscramble(key, unscramblerMap)
            }
        }
    }

    private fun unscramble(
        scrambledString: Set<ScrambledSegment>,
        unscramblerMap: Map<ScrambledSegment, Collection<UnscrambledSegment>>
    ): Int {
        val unscrambled = scrambledString.map {
            unscramblerMap[it]!!.firstOrNull()
                ?: throw IllegalStateException("Should only have one value for $it: have ${unscramblerMap[it]}")
        }.toSet()
        return segmentsToNumber[unscrambled]!!
    }
}