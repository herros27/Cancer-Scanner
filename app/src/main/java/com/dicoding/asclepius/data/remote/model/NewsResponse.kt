package com.dicoding.asclepius.data.remote.model

import com.google.gson.annotations.SerializedName

data class NewsResponse(
	@field:SerializedName("articles") val articles: List<ArticlesItem>,
)