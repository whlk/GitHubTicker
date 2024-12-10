package at.whlk.githubticker

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.mainBody
import java.io.File
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.*


fun main(args: Array<String>) = mainBody {
    val parsedArgs = ArgParser(args).parseInto(::Args)
    GitHubTicker().run(parsedArgs)
}

class GitHubTicker {

    private val props: Props = Props()

    fun run(args: Args) = with(args) {
        val text = readText(textFile)
        val cal = getCalendar(startDate)
        val output = Array2D.invoke(cal.xSize, cal.ySize) { _, _ -> false }

        val today = LocalDate.parse(currentDate)
        val startDate = cal[0, 0]
        val offset = 0
        var lastDate: LocalDate? = LocalDate.now()
        var commitToday = false
        cal.forEachIndexed { x, y, date ->
            if (x < text.xSize && x + offset < cal.xSize) {
                val dayHasText = text[x, y]
                output[x + offset, y] = dayHasText
                if (dayHasText) {
                    lastDate = date
                    if (date?.isEqual(today) == true) {
                        commitToday = true
                    }
                }
            }
        }

        props.set("startdate", startDate.toString())

        if (verbose) {
            println("start: $startDate")
            output.print()
            println("finished: $lastDate")
            println("should commit today: $commitToday")

        }
        if (commitToday && !hasCommittedToday(today)) {
            createCommits(currentDate)
            if (!dryRun) {
                pushToGithub()
            }
            saveLastCommitDate(currentDate)
        }
    }


    private fun saveLastCommitDate(currentDate: String) {
        props.set("lastcommit", currentDate)
    }

    private fun pushToGithub() {
        "git push".runCommand()
    }

    private fun createCommits(currentDate: String) {
        for (i in 1..100) {
            File("commits.txt").appendText("$currentDate\t$i\t${UUID.randomUUID()}\n")
            "git add commits.txt".runCommand()
            "git commit -m todaysdata$i".runCommand()
        }
    }

    private fun hasCommittedToday(today: LocalDate): Boolean {
        val lastCommit = props["lastcommit"] ?: "1999-01-01"
        return !LocalDate.parse(lastCommit).isBefore(today)
    }

    private fun getCalendar(startDate: String?): Array2D<LocalDate?> {
        val nextWeekStart = LocalDate.parse(startDate ?: props["startdate"] ?: LocalDate.now().toString())
                .with(DayOfWeek.SUNDAY)

        val calMatrix = Array2D.invoke<LocalDate>(52, 7)
        calMatrix.forEachIndexed { x, y, _ ->
            calMatrix[x, y] = nextWeekStart.plusWeeks(x.toLong()).plusDays(y.toLong())
        }

        return calMatrix
    }

    private fun readText(textFile: String): Array2D<Boolean> {
        val text = File(textFile).readText()
        val maxLength = (text.lines().maxByOrNull { it.length }?.length ?: 52)
        val textMatrix = Array2D.invoke(maxLength, 7) { _, _ -> false }

        val lines = text.lines()
        textMatrix.forEachIndexed { x, y, _ ->
            try {
                textMatrix[x, y] = !lines[y][x].isWhitespace()
            } catch (ignore: StringIndexOutOfBoundsException) {

            }
        }

        return textMatrix
    }
}


