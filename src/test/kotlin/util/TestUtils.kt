package util

object TestUtils {
    fun readTestData(filename: String, placeHolderValue: String) = TestUtils.javaClass
        .getResource("/$filename")
        .openStream()
        .buffered()
        .reader()
        .use { reader -> reader.readText() }
        .replace("PLACE_HOLDER", placeHolderValue)
}
