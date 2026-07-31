package moe.rukamori.archivetune

import kotlinx.coroutines.runBlocking
import moe.rukamori.archivetune.innertube.YouTube
import org.junit.Test

class YouTubeTest {
    @Test
    fun testHome() = runBlocking {
        val result = YouTube.home()
        println("Result isSuccess: ${result.isSuccess}")
        if (result.isFailure) {
            result.exceptionOrNull()?.printStackTrace()
        } else {
            println("Result: ${result.getOrNull()}")
        }
    }
}
