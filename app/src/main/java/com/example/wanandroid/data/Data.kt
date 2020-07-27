package com.example.wanandroid.data

import com.google.gson.annotations.SerializedName
import java.util.*

data class ResponseData<T>(
    var data: T?,
    var errorCode: Int,
    @SerializedName("errorMsg") var errorMessage: String?
)

data class UserData(
    @SerializedName("admin") var isAdmin: Boolean,
    var chapterTops: MutableList<Int>,
    var collectIds: MutableList<Int>,
    var email: String?,
    var icon: String?,
    var id: Int,
    var nickname: String?,
    var password: String?,
    var publicName: String?,
    var token: String?,
    var username: String?
)

data class Article(
    var id: Int,
    var apkLink: String?,
    var audit: Int,
    var author: String?,
    var canEdit: Boolean,
    var chapterId: Int,
    var chapterName: String?,
    var collect: Boolean,
    var courseId: Int,
    @SerializedName("desc") var description: String?,
    var descMd: String?,
    var envelopePic: String?,
    var fresh: Boolean,
    var link: String?,
    var niceDate: String?,
    var niceShareDate: String?,
    var origin: String?,
    var prefix: String?,
    var projectLink: String?,
    var publishTime: Long,
    var selfVisible: Int,
    var shareDate: Long,
    var shareUser: String?,
    var superChapterId: Int,
    var superChapterName: String?,
    var tags: MutableList<Tag>,
    var title: String?,
    var type: Int,
    var userId: Int,
    var visible: Int,
    var zan: Int
)

data class Tag(
    var id: Int,
    var name: String?,
    var url: String?
)

data class ArticleListResponse(
    @SerializedName("curPage") var currentPage: Int,
    var datas: MutableList<Article>?,
    var offset: Int,
    var over: Boolean,
    var pageCount: Int,
    var size: Int,
    var total: Int
)

data class ListResponse<T>(
    @SerializedName("curPage") var currentPage: Int,
    @SerializedName("datas") var items: MutableList<T>?,
    var offset: Int,
    var over: Boolean,
    var pageCount: Int,
    var size: Int,
    var total: Int
)

data class Banner(
    @SerializedName("desc") var description: String?,
    var imagePath: String?,
    var isVisible: Int,
    var order: Int,
    var title: String?,
    var type: Int,
    var url: String?
)

data class Category(
    var children: MutableList<Category>?,
    var courseId: Int,
    var id: Int,
    var name: String?,
    var order: Int,
    var parentChapterId: Int,
    var userControlSetTop: Boolean,
    var visible: Int
)

data class HotKeyword(
    var id: Int,
    var link: String?,
    var name: String?,
    var order: Int,
    var visible: Int
)

data class Site(
    var id: Int,
    var name: String,
    var link: String,
    @SerializedName("icon") var iconUrl: String,
    @SerializedName("desc") var description: String,
    var order: Int,
    var userId: Int,
    @SerializedName("visible") var isVisible: Int
)

data class Todo(
    var id: Int,
    var title: String,
    var content: String,
    @SerializedName("date") var date: Long,
    @SerializedName("dateStr") var dateText: String,
    var priority: Int,
    var status: Int,
    var type: Int,
    var userId: Int
)