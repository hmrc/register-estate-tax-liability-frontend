/*
 * Copyright 2024 HM Revenue & Customs
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

package connectors

import config.FrontendAppConfig
import models.YearsReturns
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class EstatesConnector @Inject()(http: HttpClientV2, config: FrontendAppConfig) {

  private val getDateOfDeathUrl = s"${config.estatesUrl}/estates/date-of-death"

  private val postTaxConsequences = s"${config.estatesUrl}/estates/tax-liability"

  def getDateOfDeath()(implicit hc: HeaderCarrier, ec : ExecutionContext): Future[LocalDate] = {
    //http.GET[LocalDate](getDateOfDeathUrl)
    http.get(url"$getDateOfDeathUrl").execute[LocalDate]
  }

  def saveTaxConsequence(taxYears: YearsReturns)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
  //  http.POST[JsValue, HttpResponse](postTaxConsequences, Json.toJson(taxYears))
    http.post(url"$postTaxConsequences").withBody( Json.toJson(taxYears)).execute[HttpResponse]
  }

  private lazy val resetTaxLiabilityUrl = s"${config.estatesUrl}/estates/reset-tax-liability"

  def resetTaxLiability()(implicit hc: HeaderCarrier, ec : ExecutionContext): Future[HttpResponse] = {
   // http.POSTEmpty[HttpResponse](resetTaxLiabilityUrl)
    http.post(url"$resetTaxLiabilityUrl").execute[HttpResponse]

  }

}
