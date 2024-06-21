package org.redderei.Blechwiki.util

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test

class SelectChurchTest {
    private val selectChurchTest: SelectChurch = SelectChurch()
    @Test
    fun selectChurchHasContent() {
        val expected = "N"
        assertEquals(expected, selectChurchTest.withItems("N"))
    }
}