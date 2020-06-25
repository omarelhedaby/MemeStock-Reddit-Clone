package com.example.memestock

import org.junit.Test

import org.junit.Assert.*

class passwordchangeTest {

    @Test
    fun checkpass() {
        var pas=passwordchange()
        assertEquals(pas.Checkpass("jjj","assad","adsdasd"),"OK")
    }
    @Test
    fun containsUpper() {
        var k=Register()
        var input="aAhdhhad"
        assertEquals(k.ContainsUpper(input),"OK")
    }

    @Test
    fun containsDigit() {
        var k=Register()
        var input="ahdhhad"
        assertEquals(k.ContainsDigit(input),"OK")
    }
}