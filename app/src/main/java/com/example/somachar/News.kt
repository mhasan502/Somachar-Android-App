package com.example.somachar


class News(
    private val heading: String,
    private val imageLink: String,
    private val newsLink: String,
    private val paperName: String,
    private val time: String,
    private val details: String
){

    fun getHeading(): String{
        return heading
    }
    fun getImageLink(): String{
        return imageLink
    }
    fun getNewsLink(): String{
        return newsLink
    }
    fun getPaperName(): String{
        return paperName
    }
    fun getTime(): String{
        return time
    }
    fun getDetails(): String{
        return details
    }
}
