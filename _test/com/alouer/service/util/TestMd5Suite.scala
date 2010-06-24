/**
 * 
 */
package com.alouer.service.util

import org.scalatest.FunSuite

/**
 * @author ethul
 *
 */
class TestMd5Suite extends FunSuite {
  test("md5 sum") {
    val s = "string to md5"
    val expected = "6932692e9e37c56334906bc60e02a621"
    assert(expected == Md5.sum(s))
  }
}