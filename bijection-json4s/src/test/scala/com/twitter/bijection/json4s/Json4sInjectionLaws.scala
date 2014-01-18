/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.twitter.bijection.json4s

import org.scalacheck.Properties
import com.twitter.bijection.{Injection, BaseProperties}
import Json4sInjections._
import org.json4s.JsonAST._
import org.json4s.JsonAST.JString

/**
 * @author Mansur Ashraf
 * @since 1/10/14
 */
object Json4sInjectionLaws extends Properties("Json4sInjection")
with BaseProperties {
  case class Twit(name: String, id: Int, id_str: String, indices: List[Int], screen_name: String)

  def createTwit(i: (String, Int, String, List[Int], String)): Twit = Twit(i._1, i._2, i._3, i._4, i._5)

  implicit val testCaseClassToJson = arbitraryViaFn {
    in: (String, Int, String, List[Int], String) => createTwit(in)
  }

  implicit val testJValueToJson = arbitraryViaFn[(String, Int, String, List[Int], String),JValue] {
    in: (String, Int, String, List[Int], String) => JObject(
      List(
        JField("name", JString(in._1)),
        JField("id", JInt(in._2)),
        JField("id_String", JString(in._3)),
        JField("indices", JArray(in._4.map(JInt(_)))),
        JField("screen_name", JString(in._5))
      ))
  }

  def roundTripCaseClassToJson[T <: AnyRef](implicit inj: Injection[T, String], mn: Manifest[T]) = isLooseInjection[Twit, String]

  def roundTripCaseClassToJValue[T <: AnyRef](implicit inj: Injection[T, JValue], mn: Manifest[T]) = isLooseInjection[Twit, JValue]

  def roundTripJValueToString(implicit inj: Injection[JValue, String]) = isLooseInjection[JValue, String]

  property("round trip Case Class to Json") = roundTripCaseClassToJson
  property("round trip Case Class to JValue") = roundTripCaseClassToJValue
  property("round trip JValue to String") = roundTripJValueToString
}
