package com.project.todoapp.models

import java.io.Serializable

class Article(
    var author: String?,
    var content: String?,
    var description: String?,
    var publishedAt: String?,
    var title: String?,
    var url: String,
    var urlToImage: String
): Serializable