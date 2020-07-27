package com.example.wanandroid.data

import com.example.wanandroid.data.remote.ToolService
import com.example.wanandroid.utils.Transformers.processMutableListResponse
import com.example.wanandroid.utils.Transformers.processResponse
import io.reactivex.Observable
import javax.inject.Inject

class ToolRepository @Inject constructor(private val service: ToolService) {
    fun fetchStarredSites(authenticateCookie: String): Observable<Result<MutableList<Site>>> {
        return service.fetchMarkedSites(authenticateCookie).compose(processMutableListResponse())
    }

    fun addSite(authenticateCookie: String, name: String, url: String): Observable<Result<Site>> {
        return service.addSite(authenticateCookie, name, url).compose(processResponse())
    }

    fun editSite(
        authenticateCookie: String, id: Int, name: String, url: String
    ): Observable<Result<Site>> {
        return service.editSite(authenticateCookie, id, name, url).compose(processResponse())
    }

    fun deleteSite(authenticateCookie: String, id: Int): Observable<Result<Unit>> {
        return service.deleteSite(authenticateCookie, id).compose(processResponse())
    }
}