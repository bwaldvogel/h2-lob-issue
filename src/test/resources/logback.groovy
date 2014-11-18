appender("CONSOLE", ConsoleAppender) {
  encoder(PatternLayoutEncoder) {
    pattern = "%d{HH:mm:ss.SSS} %-5level [%thread] [%logger{30}] - %msg%n"
  }
}
root(INFO, ["CONSOLE"])

logger("org.h2", TRACE)

logger("de.bwaldvogel", DEBUG)