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

package utils

import base.SpecBase
import controllers.routes
import models.{CYMinus1TaxYear, CYMinus2TaxYear, CYMinus3TaxYear, CYMinus4TaxYear, NormalMode, TaxYearRange}
import pages._
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

class CheckYourAnswersHelperSpec extends SpecBase {

  val cYMinus4StartYear: String = TaxYearRange(CYMinus4TaxYear).yearAtStart
  val cYMinus3StartYear: String = TaxYearRange(CYMinus3TaxYear).yearAtStart

  "Check your answers helper" when {

    "earlier years" must {

      "render answers for tax before 4 years" in {
        val cyaHelper = injector.instanceOf[CheckYourAnswersHelper]

        val userAnswers = emptyUserAnswers
          .set(CYMinusFourEarlierYearsYesNoPage, true).success.value

        val result = cyaHelper.earlierThan4YearsAnswers(userAnswers)

        result.value mustBe AnswerSection(
          heading = Some(Html(messages("earlierYearsLiability.checkYourAnswerSectionHeading", cYMinus4StartYear))),
          rows = Seq(
            AnswerRow(label = Html(messages("earlierYearsLiability.checkYourAnswersLabel", cYMinus4StartYear)), answer = Html("Yes"),
              changeUrl = routes.CYMinusFourEarlierYearsLiabilityController.onPageLoad(NormalMode).url
            )
          )
        )

      }

      "render answers for tax before 3 years" in {
        val cyaHelper = injector.instanceOf[CheckYourAnswersHelper]

        val userAnswers = emptyUserAnswers
          .set(CYMinusThreeEarlierYearsYesNoPage, true).success.value

        val result = cyaHelper.earlierThan3YearsAnswers(userAnswers)

        result.value mustBe AnswerSection(
          heading = Some(Html(messages("earlierYearsLiability.checkYourAnswerSectionHeading", cYMinus3StartYear))),
          rows = Seq(
            AnswerRow(label = Html(messages("earlierYearsLiability.checkYourAnswersLabel", cYMinus3StartYear)), answer = Html("Yes"),
              changeUrl = routes.CYMinusThreeEarlierYearsLiabilityController.onPageLoad(NormalMode).url
            )
          )
        )
      }

    }

    "CY-4" must {

      "render answers for CY-4 liability and declared" in {
        val cyaHelper = injector.instanceOf[CheckYourAnswersHelper]

        val taxYear = CYMinus4TaxYear
        val taxYearRange: String = TaxYearRange(taxYear).toRange

        val userAnswers = emptyUserAnswers
          .set(CYMinusFourYesNoPage, true).success.value
          .set(DidDeclareTaxToHMRCYesNoPage(taxYear), true).success.value

        val result = cyaHelper.cyMinusTaxYearAnswers(userAnswers, taxYear)

        result.value mustBe AnswerSection(
          heading = Some(Html(messages("taxLiabilityBetweenYears.checkYourAnswerSectionHeading", taxYearRange))),
          rows = Seq(
            AnswerRow(
              label = Html(messages("cyMinusFour.liability.checkYourAnswersLabel", taxYearRange)),
              answer = Html("Yes"),
              changeUrl = routes.CYMinusFourLiabilityController.onPageLoad(NormalMode).url
            ),
            AnswerRow(
              label = Html(messages("didDeclareToHMRC.checkYourAnswersLabel", taxYearRange)),
              answer = Html("Yes"),
              changeUrl = routes.DidDeclareTaxToHMRCController.onPageLoad(NormalMode, taxYear).url
            )
          )
        )
      }
    }

    "CY-3" must {

      "render answers for CY-3 liability and declared" in {
        val cyaHelper = injector.instanceOf[CheckYourAnswersHelper]

        val taxYear = CYMinus3TaxYear
        val taxYearRange: String = TaxYearRange(taxYear).toRange

        val userAnswers = emptyUserAnswers
          .set(CYMinusThreeYesNoPage, true).success.value
          .set(DidDeclareTaxToHMRCYesNoPage(taxYear), true).success.value

        val result = cyaHelper.cyMinusTaxYearAnswers(userAnswers, taxYear)

        result.value mustBe AnswerSection(
          heading = Some(Html(messages("taxLiabilityBetweenYears.checkYourAnswerSectionHeading", taxYearRange))),
          rows = Seq(
            AnswerRow(
              label = Html(messages("cyMinusThree.liability.checkYourAnswersLabel", taxYearRange)),
              answer = Html("Yes"),
              changeUrl = routes.CYMinusThreeLiabilityController.onPageLoad(NormalMode).url
            ),
            AnswerRow(
              label = Html(messages("didDeclareToHMRC.checkYourAnswersLabel", taxYearRange)),
              answer = Html("Yes"),
              changeUrl = routes.DidDeclareTaxToHMRCController.onPageLoad(NormalMode, taxYear).url
            )
          )
        )
      }
    }

    "CY-2" must {

      "render answers for CY-2 liability and declared" in {
        val cyaHelper = injector.instanceOf[CheckYourAnswersHelper]

        val taxYear = CYMinus2TaxYear
        val taxYearRange: String = TaxYearRange(taxYear).toRange

        val userAnswers = emptyUserAnswers
          .set(CYMinusTwoYesNoPage, true).success.value
          .set(DidDeclareTaxToHMRCYesNoPage(taxYear), true).success.value

        val result = cyaHelper.cyMinusTaxYearAnswers(userAnswers, taxYear)

        result.value mustBe AnswerSection(
          heading = Some(Html(messages("taxLiabilityBetweenYears.checkYourAnswerSectionHeading", taxYearRange))),
          rows = Seq(
            AnswerRow(
              label = Html(messages("cyMinusTwo.liability.checkYourAnswersLabel", taxYearRange)),
              answer = Html("Yes"),
              changeUrl = routes.CYMinusTwoLiabilityController.onPageLoad(NormalMode).url
            ),
            AnswerRow(
              label = Html(messages("didDeclareToHMRC.checkYourAnswersLabel", taxYearRange)),
              answer = Html("Yes"),
              changeUrl = routes.DidDeclareTaxToHMRCController.onPageLoad(NormalMode, taxYear).url
            )
          )
        )
      }
    }

    "CY-1" must {

      "render answers for CY-1 liability and declared" in {
        val cyaHelper = injector.instanceOf[CheckYourAnswersHelper]

        val taxYear = CYMinus1TaxYear
        val taxYearRange: String = TaxYearRange(taxYear).toRange

        val userAnswers = emptyUserAnswers
          .set(CYMinusOneYesNoPage, true).success.value
          .set(DidDeclareTaxToHMRCYesNoPage(taxYear), true).success.value

        val result = cyaHelper.cyMinusTaxYearAnswers(userAnswers, taxYear)

        result.value mustBe AnswerSection(
          heading = Some(Html(messages("taxLiabilityBetweenYears.checkYourAnswerSectionHeading", taxYearRange))),
          rows = Seq(
            AnswerRow(
              label = Html(messages("cyMinusOne.liability.checkYourAnswersLabel", taxYearRange)),
              answer = Html("Yes"),
              changeUrl = routes.CYMinusOneLiabilityController.onPageLoad(NormalMode).url
            ),
            AnswerRow(
              label = Html(messages("didDeclareToHMRC.checkYourAnswersLabel", taxYearRange)),
              answer = Html("Yes"),
              changeUrl = routes.DidDeclareTaxToHMRCController.onPageLoad(NormalMode, taxYear).url
            )
          )
        )
      }
    }

  }

}
