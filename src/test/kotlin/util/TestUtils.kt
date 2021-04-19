package util

object TestUtils {
    fun readTestData(filename: String) = getResource(filename)
        .openStream()
        .buffered()
        .reader()
        .use { reader -> reader.readText() }

    private fun getResource(filename: String) =
        TestUtils::class.java.getResource("/$filename")
            ?: throw IllegalStateException("Can't read test data file $filename from resources!")
}
