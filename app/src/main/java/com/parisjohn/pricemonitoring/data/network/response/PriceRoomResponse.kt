package com.parisjohn.pricemonitoring.data.network.response


import com.google.gson.annotations.SerializedName

data class PriceRoomResponse(
    @SerializedName("_embedded")
    val embedded: Embedded,
    @SerializedName("_links")
    val links: Links,
    @SerializedName("page")
    val page: Page
) {
    data class Embedded(
        @SerializedName("priceInfoList")
        var priceInfoList: List<PriceInfo>
    ) {
        data class PriceInfo(
            @SerializedName("attributes")
            val attributes: List<String>,
            @SerializedName("breakfastPolicy")
            val breakfastPolicy: String,
            @SerializedName("cancellationPolicy")
            val cancellationPolicy: String,
            @SerializedName("distanceDays")
            val distanceDays: Int,
            @SerializedName("id")
            val id: Int,
            @SerializedName("price")
            val price: Int,
            @SerializedName("quantity")
            val quantity: Int,
            @SerializedName("sleeps")
            val sleeps: Int,
            @SerializedName("timestamp")
            val timestamp: String,

            val date: String
        )
    }

    data class Links(
        @SerializedName("first")
        val first: First,
        @SerializedName("last")
        val last: Last,
        @SerializedName("next")
        val next: Next,
        @SerializedName("self")
        val self: Self
    ) {
        data class First(
            @SerializedName("href")
            val href: String
        )

        data class Last(
            @SerializedName("href")
            val href: String
        )

        data class Next(
            @SerializedName("href")
            val href: String
        )

        data class Self(
            @SerializedName("href")
            val href: String
        )
    }

    data class Page(
        @SerializedName("number")
        val number: Int,
        @SerializedName("size")
        val size: Int,
        @SerializedName("totalElements")
        val totalElements: Int,
        @SerializedName("totalPages")
        val totalPages: Int
    )
}