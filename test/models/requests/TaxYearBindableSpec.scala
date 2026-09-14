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

package models.requests

import base.SpecBase
import models.{CYMinus1TaxYear, CYMinus2TaxYear, CYMinus3TaxYear, CYMinus4TaxYear, TaxYear}
import play.api.mvc.PathBindable

class TaxYearBindableSpec extends SpecBase {

  private val bindable: PathBindable[TaxYear] = TaxYearBindable.pathBindable

  "TaxYearBindable" must {

    "bind every tax year the service asks about" in {
      bindable.bind("taxYear", "1") mustBe Right(CYMinus1TaxYear)
      bindable.bind("taxYear", "2") mustBe Right(CYMinus2TaxYear)
      bindable.bind("taxYear", "3") mustBe Right(CYMinus3TaxYear)
      bindable.bind("taxYear", "4") mustBe Right(CYMinus4TaxYear)
    }

    "reject a year outside that range" in {
      bindable.bind("taxYear", "5") mustBe Left("Not a valid tax year")
    }

    "reject something that is not a year at all" in {
      bindable.bind("taxYear", "not-a-number").isLeft mustBe true
    }

    "unbind back to the number of years" in {
      bindable.unbind("taxYear", CYMinus3TaxYear) mustBe "3"
    }
  }

}
