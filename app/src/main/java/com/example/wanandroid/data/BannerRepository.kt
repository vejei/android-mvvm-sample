package com.example.wanandroid.data

import com.example.wanandroid.data.remote.BannerService
import com.example.wanandroid.utils.Transformers.processResponse
import io.reactivex.Observable
import javax.inject.Inject

class BannerRepository @Inject constructor(private val service: BannerService) {

    fun fetchBanner(): Observable<Result<MutableList<Banner>>> {
//        return service.fetchBanner().compose(asyncRequest()).compose(mapResult())
        return service.fetchBanner().compose(processResponse())
    }
}