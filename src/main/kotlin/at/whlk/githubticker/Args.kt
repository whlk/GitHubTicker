package at.whlk.githubticker

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.InvalidArgumentException
import com.xenomachina.argparser.default
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeParseException

class Args(parser: ArgParser) {
    val verbose by parser.flagging(
            "-v", "--verbose",
            help = "enable verbose mode")

    val dryRun by parser.flagging(
            "-d", "--dryRun",
            help = "dry run of the script (no commits will be pushed)")

    val textFile by parser.storing(
            "-f", "--file",
            help = "path to the ticker text file")
            .default("text.txt")
            .addValidator {
                if (!File(value).exists()) {
                    throw InvalidArgumentException("The file $value does not exist. Please create it with your ticker message first.")
                }
            }

    val startDate by parser.storing("-s", "--start",
            help = "day for the start of the script (yyyy-mm-dd)")
            .default<String?>(null)
            .addValidator {
                if (value == null) return@addValidator
                try {
                    LocalDate.parse(value)
                } catch (e: DateTimeParseException) {
                    throw InvalidArgumentException("Invalid date '$value'. Try yyyy-mm-dd")
                }
            }

    val currentDate by parser.storing("-n", "--now",
            help = "the current day (format: yyyy-mm-dd). System time will be used otherwise")
            .default<String>(LocalDate.now().toString())
            .addValidator {
                try {
                    LocalDate.parse(value)
                } catch (e: DateTimeParseException) {
                    throw InvalidArgumentException("Invalid date '$value'. Try yyyy-mm-dd")
                }
            }
}