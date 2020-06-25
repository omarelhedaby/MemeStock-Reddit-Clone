package com.example.memestock

import org.junit.Test

import org.junit.Assert.*

class SearchTest {

    @Test
    fun contains_ignore_case() {
        val k=Search()
        var e= arrayListOf<String>("OMAR","blaBla")
        var h="blablaa"
        assert(k.contains_ignore_case(e,h))
    }
    @Test
    fun userOrSubreddit ()
    {
        val s=Search()
        var arr:String="r/egypt"
        assertEquals(s.userOrSubreddit(arr),"subreddit")
    }
}