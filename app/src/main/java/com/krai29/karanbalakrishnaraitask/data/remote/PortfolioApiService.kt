package com.krai29.karanbalakrishnaraitask.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface PortfolioApi {

    @GET(".")
    suspend fun getHoldings(
        @Query("page") page: Int? = null,
        @Query("pageSize") pageSize: Int? = null
    ): PortfolioResponseDto
}