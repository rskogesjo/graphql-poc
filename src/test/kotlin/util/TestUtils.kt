package util

object TestUtils {
    fun readTestData(filename: String) = TestUtils.javaClass
        .getResource("/$filename")
        ?.openStream()
        ?.buffered()
        ?.reader()
        .use { reader -> reader?.readText() }
}
