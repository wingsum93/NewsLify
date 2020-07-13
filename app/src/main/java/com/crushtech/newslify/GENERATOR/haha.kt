package com.crushtech.newslify.GENERATOR

import kotlin.random.Random

class haha {
    companion object {
        val list = listOf(
            "a0b90969cd63469b81164236f674b2cd",
            "37cade18ac8b43178465a5e8eb3337f9",
            "0c377f4551184aaca432bf4fefc94596",
            "0c377f4551184aaca432bf4fefc94596",
            "a49d820b710149bab342da187bee15c0",
            "12796f76ac814612bb58f572bfc4a5a0",
            "5fcb5146577046a18786bb167c63e1e9",
            "eef0919258984c6fa5e513250aea3c1e",
            "df8fab08721f40438c234de2b23ffb76",
            "94f84f88af9d464da16766c9fb4a9f69",
            "91fa063c8d8643e0a0aed7a4e5ddc49b",
            "40ddedebd93d420e982d46168aa60f2f"
        )
        private val randomValue = Random.nextInt(list.size)
        val API_KEY = list[randomValue]

    }
}
