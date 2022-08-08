/*
 * Copyright 2022 HM Revenue & Customs
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
import uk.gov.hmrc.play.language.LanguageUtils
import viewmodels.{AnswerRow, AnswerSection}

class CheckYourAnswersHelperSpec extends SpecBase {

  val languageUtils: LanguageUtils = injector.instanceOf[LanguageUtils]
  val taxYearRange: TaxYearRange = new TaxYearRange(languageUtils)

  val cYMinus4StartYear: String = taxYearRange.yearAtStart(CYMinus4TaxYear)
  val cYMinus3StartYear: String = taxYearRange.yearAtStart(CYMinus3TaxYear)

  "Check your answers helper" when {

    "earlier years" must {

      "render answers for tax before 4 years" in {
        val cyaHelper = injector.instanceOf[CheckYourAnswersHelper]

        val userAnswers = emptyUserAnswers
          .set(CYMinusFourEarlierYearsYesNoPage, true).success.value

        val result = cyaHelper.earlierThan4YearsAnswers(userAnswers)

        result.value mustBe AnswerSection(
          heading = Some(Html(messages("earlierYearsLiabilityYesNo.checkYourAnswerSectionHeading", cYMinus4StartYear))),
          rows = Seq(
            AnswerRow(label = messages("earlierYearsLiabilityYesNo.checkYourAnswersLabel", cYMinus4StartYear), answer = Html("Yes"),
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
          heading = Some(Html(messages("earlierYearsLiabilityYesNo.checkYourAnswerSectionHeading", cYMinus3StartYear))),
          rows = Seq(
            AnswerRow(label = messages("earlierYearsLiabilityYesNo.checkYourAnswersLabel", cYMinus3StartYear), answer = Html("Yes"),
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
        val taxYearRangeDisplay: String = taxYearRange.toRange(taxYear)

        val userAnswers = emptyUserAnswers
          .set(CYMinusFourYesNoPage, true).success.value
          .set(DidDeclareTaxToHMRCYesNoPage(taxYear), true).success.value

        val result = cyaHelper.cyMinusTaxYearAnswers(userAnswers, taxYear)

        result.value mustBe AnswerSection(
          heading = Some(Html(messages("taxLiabilityBetweenYears.checkYourAnswerSectionHeading", taxYearRangeDisplay))),
          rows = Seq(
            AnswerRow(
              label = messages("cyMinusFourYesNo.liability.checkYourAnswersLabel", taxYearRangeDisplay),
              answer = Html("Yes"),
              changeUrl = routes.CYMinusFourLiabilityController.onPageLoad(NormalMode).url
            ),
            AnswerRow(
              label = messages("didDeclareToHMRCYesNo.checkYourAnswersLabel", taxYearRangeDisplay),
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
        val taxYearRangeDisplay: String = taxYearRange.toRange(taxYear)

        val userAnswers = emptyUserAnswers
          .set(CYMinusThreeYesNoPage, true).success.value
          .set(DidDeclareTaxToHMRCYesNoPage(taxYear), true).success.value

        val result = cyaHelper.cyMinusTaxYearAnswers(userAnswers, taxYear)

        result.value mustBe AnswerSection(
          heading = Some(Html(messages("taxLiabilityBetweenYears.checkYourAnswerSectionHeading", taxYearRangeDisplay))),
          rows = Seq(
            AnswerRow(
              label = messages("cyMinusThreeYesNo.liability.checkYourAnswersLabel", taxYearRangeDisplay),
              answer = Html("Yes"),
              changeUrl = routes.CYMinusThreeLiabilityController.onPageLoad(NormalMode).url
            ),
            AnswerRow(
              label = messages("didDeclareToHMRCYesNo.checkYourAnswersLabel", taxYearRangeDisplay),
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
        val taxYearRangeDisplay: String = taxYearRange.toRange(taxYear)

        val userAnswers = emptyUserAnswers
          .set(CYMinusTwoYesNoPage, true).success.value
          .set(DidDeclareTaxToHMRCYesNoPage(taxYear), true).success.value

        val result = cyaHelper.cyMinusTaxYearAnswers(userAnswers, taxYear)

        result.value mustBe AnswerSection(
          heading = Some(Html(messages("taxLiabilityBetweenYears.checkYourAnswerSectionHeading", taxYearRangeDisplay))),
          rows = Seq(
            AnswerRow(
              label = messages("cyMinusTwoYesNo.liability.checkYourAnswersLabel", taxYearRangeDisplay),
              answer = Html("Yes"),
              changeUrl = routes.CYMinusTwoLiabilityController.onPageLoad(NormalMode).url
            ),
            AnswerRow(
              label = messages("didDeclareToHMRCYesNo.checkYourAnswersLabel", taxYearRangeDisplay),
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
        val taxYearRangeDisplay: String = taxYearRange.toRange(taxYear)

        val userAnswers = emptyUserAnswers
          .set(CYMinusOneYesNoPage, true).success.value
          .set(DidDeclareTaxToHMRCYesNoPage(taxYear), true).success.value

        val result = cyaHelper.cyMinusTaxYearAnswers(userAnswers, taxYear)

        result.value mustBe AnswerSection(
          heading = Some(Html(messages("taxLiabilityBetweenYears.checkYourAnswerSectionHeading", taxYearRangeDisplay))),
          rows = Seq(
            AnswerRow(
              label = messages("cyMinusOneYesNo.liability.checkYourAnswersLabel", taxYearRangeDisplay),
              answer = Html("Yes"),
              changeUrl = routes.CYMinusOneLiabilityController.onPageLoad(NormalMode).url
            ),
            AnswerRow(
              label = messages("didDeclareToHMRCYesNo.checkYourAnswersLabel", taxYearRangeDisplay),
              answer = Html("Yes"),
              changeUrl = routes.DidDeclareTaxToHMRCController.onPageLoad(NormalMode, taxYear).url
            )
          )
        )
      }
    }

  }

}
