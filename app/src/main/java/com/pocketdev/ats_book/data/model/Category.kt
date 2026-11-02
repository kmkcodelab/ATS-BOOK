package com.pocketdev.ats_book.data.model

data class Category(
    val id: String,
    val name: String,
    val displayNameBangla: String
) {
    companion object {
        val ALL = listOf(
            Category(id = "all", name = "All", displayNameBangla = "সব গল্প"),
            Category(id = "Romance", name = "Romance", displayNameBangla = "রোমান্স"),
            Category(id = "Horror", name = "Horror", displayNameBangla = "ভৌতিক"),
            Category(id = "Mystery", name = "Mystery", displayNameBangla = "রহস্য"),
            Category(id = "Motivational", name = "Motivational", displayNameBangla = "অনুপ্রেরণা"),
            Category(id = "Comedy", name = "Comedy", displayNameBangla = "হাসির গল্প")
        )
    }
}
