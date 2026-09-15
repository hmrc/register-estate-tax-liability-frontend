/*
 * Copyright 2026 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package models

import base.SpecBase
import pages.{CYMinusOneYesNoPage, DidDeclareTaxToHMRCYesNoPage}
import play.api.libs.json.{JsNull, JsObject, Json}
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats

import java.time.Instant

class UserAnswersSpec extends SpecBase {

  private val instant = Instant.ofEpochMilli(1517443200000L)

  "UserAnswers" must {

    "start a new session with no answers" in {
      val answers = UserAnswers.startNewSession("internal-id")

      answers.id   mustBe "internal-id"
      answers.data mustBe Json.obj()
    }

    "read a question that has been answered" in {
      val answers = emptyUserAnswers.set(CYMinusOneYesNoPage, true).success.value

      answers.get(CYMinusOneYesNoPage) mustBe Some(true)
    }

    "read nothing for a question that has not been answered" in {
      emptyUserAnswers.get(CYMinusOneYesNoPage) mustBe None
    }

    "read nothing when the stored answer is the wrong type" in {
      val answers = emptyUserAnswers.copy(data = Json.obj("cyMinusOneYesNo" -> "not a boolean"))

      answers.get(CYMinusOneYesNoPage) mustBe None
    }

    "overwrite a previous answer" in {
      val answers = emptyUserAnswers
        .set(CYMinusOneYesNoPage, true)
        .flatMap(_.set(CYMinusOneYesNoPage, false))
        .success
        .value

      answers.get(CYMinusOneYesNoPage) mustBe Some(false)
    }

    "clean up the answers a changed answer makes irrelevant" in {
      val answers = emptyUserAnswers
        .set(CYMinusOneYesNoPage, true)
        .flatMap(_.set(DidDeclareTaxToHMRCYesNoPage(CYMinus1TaxYear), true))
        .flatMap(_.set(CYMinusOneYesNoPage, false))
        .success
        .value

      answers.get(CYMinusOneYesNoPage)                           mustBe Some(false)
      answers.get(DidDeclareTaxToHMRCYesNoPage(CYMinus1TaxYear)) mustBe None
    }

    "remove an answer" in {
      val answers = emptyUserAnswers
        .set(CYMinusOneYesNoPage, true)
        .flatMap(_.remove(CYMinusOneYesNoPage))
        .success
        .value

      answers.get(CYMinusOneYesNoPage) mustBe None
    }

    "clear a question that was never answered without failing" in {
      val answers = emptyUserAnswers.remove(CYMinusOneYesNoPage).success.value

      answers.data                     mustBe Json.obj("cyMinusOneYesNo" -> JsNull)
      answers.get(CYMinusOneYesNoPage) mustBe None
    }

    "round trip through the format Mongo stores it in" in {
      val answers = UserAnswers("id", Json.obj("cyMinusOneYesNo" -> true), instant)

      val json = Json.toJson(answers)(UserAnswers.writes)

      (json \ "_id").as[String]                                             mustBe "id"
      (json \ "data").as[JsObject]                                          mustBe Json.obj("cyMinusOneYesNo" -> true)
      (json \ "lastUpdated").as[Instant](MongoJavatimeFormats.instantReads) mustBe instant

      json.as[UserAnswers](UserAnswers.reads) mustBe answers
    }
  }

}
