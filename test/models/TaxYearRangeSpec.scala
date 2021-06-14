/*
 * Copyright 2021 HM Revenue & Customs
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
import org.scalatest.BeforeAndAfterEach
import play.api.i18n.{Lang, MessagesImpl}
import uk.gov.hmrc.play.language.LanguageUtils

class TaxYearRangeSpec extends SpecBase with BeforeAndAfterEach {

  val languageUtils: LanguageUtils = injector.instanceOf[LanguageUtils]

  "TaxYearRange" when {

    val languageUtils: LanguageUtils = injector.instanceOf[LanguageUtils]
    val taxYearRange: TaxYearRange = new TaxYearRange(languageUtils)

    ".taxYearDates" must {
      "return tax year start date and end date as list of strings" when {

        def messages(langCode: String): MessagesImpl = {
          val lang: Lang = Lang(langCode)
          MessagesImpl(lang, messagesApi)
        }

        "CYMinus1TaxYear" when {

          val taxYear = CYMinus1TaxYear

          "English" in {
            val result = taxYearRange.toRange(taxYear)(messages("en"))
            result mustEqual Seq("6 April 2019", "5 April 2020")
          }

          "Welsh" in {
            val result = taxYearRange.toRange(taxYear)(messages("cy"))
            result mustEqual Seq("6 Ebrill 2019", "5 Ebrill 2020")
          }
        }

        "CYMinus2TaxYear" when {

          val taxYear = CYMinus2TaxYear

          "English" in {
            val result = taxYearRange.toRange(taxYear)(messages("en"))
            result mustEqual Seq("6 April 2018", "5 April 2019")
          }

          "Welsh" in {
            val result = taxYearRange.toRange(taxYear)(messages("cy"))
            result mustEqual Seq("6 Ebrill 2018", "5 Ebrill 2019")
          }
        }

        "CYMinus3TaxYear" when {

          val taxYear = CYMinus3TaxYear

          "English" in {
            val result = taxYearRange.toRange(taxYear)(messages("en"))
            result mustEqual Seq("6 April 2017", "5 April 2018")
          }

          "Welsh" in {
            val result = taxYearRange.toRange(taxYear)(messages("cy"))
            result mustEqual Seq("6 Ebrill 2017", "5 Ebrill 2018")
          }
        }

        "CYMinus4TaxYear" when {

          val taxYear = CYMinus4TaxYear

          "English" in {
            val result = taxYearRange.toRange(taxYear)(messages("en"))
            result mustEqual Seq("6 April 2016", "5 April 2017")
          }

          "Welsh" in {
            val result = taxYearRange.toRange(taxYear)(messages("cy"))
            result mustEqual Seq("6 Ebrill 2016", "5 Ebrill 2017")
          }
        }
      }
    }
  }
}
