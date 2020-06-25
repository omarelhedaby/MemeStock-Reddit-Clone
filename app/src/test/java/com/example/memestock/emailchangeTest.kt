package com.example.memestock

import org.junit.Test

import org.junit.Assert.*

class emailchangeTest {

    @Test
    fun mailvalidity() {
        var ma=emailchange()
        var input="ahdhhad@DD.COM"
        assertEquals(ma.mailvalidity(input),"OK")
    }
}