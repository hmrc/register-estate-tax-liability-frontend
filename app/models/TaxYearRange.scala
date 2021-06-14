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

import javax.inject.Inject
import play.api.i18n.Messages
import uk.gov.hmrc.play.language.LanguageUtils

class TaxYearRange @Inject()(languageUtils: LanguageUtils) {

  private def taxYearYear(taxYear: TaxYear) = uk.gov.hmrc.time.TaxYear.current.back(taxYear.year)

  def startYear(taxYear: TaxYear)(implicit messages: Messages): String = languageUtils.Dates.formatDate(taxYearYear(taxYear).starts)
  def endYear(taxYear: TaxYear)(implicit messages: Messages): String = languageUtils.Dates.formatDate(taxYearYear(taxYear).finishes)

  def yearAtStart(taxYear: TaxYear): String = taxYearYear(taxYear).startYear.toString

  def toRange(taxYear: TaxYear)(implicit messages: Messages) : String = {
    messages("taxYearToRange", startYear(taxYear), endYear(taxYear))
  }

  def andRange(taxYear: TaxYear)(implicit messages: Messages) : String = {
    messages("taxYearAndRange", startYear(taxYear), endYear(taxYear))
  }

}
