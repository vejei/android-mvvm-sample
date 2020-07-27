package com.example.wanandroid.testing

/**
 * This annotation allows us to open some classes for mocking purposes while they are final in
 * release builds.
 */
@Target(AnnotationTarget.ANNOTATION_CLASS)
annotation class OpenClass

/**
 * Annotate a class with [TestingOpen] if you want it to be extendable in debug builds.
 */
@OpenClass
@Target(AnnotationTarget.CLASS)
annotation class TestingOpen