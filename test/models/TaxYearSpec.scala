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

class TaxYearSpec extends SpecBase {

  private val allYears = Seq(CYMinus1TaxYear, CYMinus2TaxYear, CYMinus3TaxYear, CYMinus4TaxYear)

  "TaxYear" must {

    "count back from the current year" in {
      allYears.map(_.year) mustBe Seq(1, 2, 3, 4)
    }

    "know which set of messages describes it" in {
      allYears.map(_.messagePrefix) mustBe
        Seq("cyMinusOneYesNo", "cyMinusTwoYesNo", "cyMinusThreeYesNo", "cyMinusFourYesNo")
    }

    "render as the number of years back, for URLs and reverse routes" in {
      allYears.map(_.toString)           mustBe Seq("1", "2", "3", "4")
      allYears.map(TaxYear.jsLiteral.to) mustBe Seq("1", "2", "3", "4")
    }

    "give the last two digits of the year that tax year finished in" in
      allYears.foreach { taxYear =>
        val expected = uk.gov.hmrc.time.TaxYear.current.back(taxYear.year).finishYear.toString.takeRight(2)
        taxYear.asShortFinishYear() mustBe expected
      }

    "be recognised from the number of years back" in
      allYears.foreach(taxYear => TaxYear.from(taxYear.year) mustBe Some(taxYear))

    "not be recognised from a year outside the range the service asks about" in
      Seq(-1, 0, 5, 100).foreach(year => TaxYear.from(year) mustBe None)
  }

}
