package com.pocketdev.ats_book.data.model

data class Story(
    val id: Int,
    val title: String,
    val category: String,
    val author: String,
    val content: String,
    val isLocked: Boolean = false,
    val readTime: String = "5 min"
)
