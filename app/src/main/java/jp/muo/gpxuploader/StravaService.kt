package jp.muo.gpxuploader

import retrofit2.http.Field
import retrofit2.http.POST

interface StravaService {
    @POST("/uploads")
    fun uploadActivity(@Field("file"))
}