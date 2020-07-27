package com.example.wanandroid

import com.example.wanandroid.data.*

object TestData {
    val userData = UserData(
        isAdmin = false,
        chapterTops = mutableListOf(),
        collectIds = mutableListOf(),
        email = "foo@bar.com",
        icon = "icon.png",
        id = 10,
        nickname = "nickname",
        password = "12345",
        publicName = "public name",
        token = "token",
        username = "username"
    )

    private val article = Article(id = 1, apkLink = "https://foo.com/bar", audit = 1, author = "foo",
        canEdit = false, chapterId = 1, chapterName = "abc", collect = false, courseId = 1,
        description = "the description", descMd = null, envelopePic = "https://foo.com/bar.png",
        fresh = false, link = "https://foo.com/bar", niceDate = "3030-13-32",
        niceShareDate = "3030-13-32", origin = null, prefix = null, projectLink = null,
        publishTime = 123456789, selfVisible = 1, shareDate = 123456789, shareUser = "foo",
        superChapterId = 1, superChapterName = "bar", tags = emptyList<Tag>().toMutableList(),
        title = "this is the title", type = 1, userId = 1, visible = 1, zan = 100000)
    val articleData = mutableListOf(article, article, article)

    private val carousel = Banner(description = "the description", imagePath = "https://foo.com/bar",
        isVisible = 1, order = 1, title = "title", type = 1, url = "https://foo.com/bar")
    val carouselData = mutableListOf(carousel, carousel, carousel)

    private val category = Category(children = mutableListOf(), courseId = 0, id = 0, name = "foo",
        order = 0, parentChapterId = 0, userControlSetTop = true, visible = 1)
    val categoriesData = mutableListOf(category, category, category, category, category)
}