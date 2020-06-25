package com.example.memestock

import org.junit.Test

import org.junit.Assert.*

class RegisterTest {

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

    @Test
    fun isEmailValid() {
        var k=Register()
        var input="ahdhhad@DD.COM"
        assertEquals(k.isEmailValid(input),"OK")
    }
}