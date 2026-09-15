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

package handlers

import base.SpecBase
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewUtils.breadcrumbTitle

class ErrorHandlerSpec extends SpecBase {

  private val handler = applicationBuilder().build().injector.instanceOf[ErrorHandler]

  private def errorPage(pageTitle: String, heading: String, message: String): Document =
    Jsoup.parse(handler.standardErrorTemplate(pageTitle, heading, message)(fakeRequest).futureValue.toString)

  "ErrorHandler" must {

    "render the standard error page inside the service's own template" in {

      val doc = errorPage("Page not found", "This page cannot be found", "Check the web address is correct.")

      doc.title                               mustBe breadcrumbTitle("Page not found")
      doc.getElementsByTag("h1").text         mustBe "This page cannot be found"
      doc.getElementsByClass("govuk-body").text must include("Check the web address is correct.")
    }

    "keep the service navigation component on the error page" in {

      val doc = errorPage("title", "heading", "message")

      doc.getElementsByClass("govuk-service-navigation__service-name").text().trim mustBe messages("service.name")
    }
  }

}
