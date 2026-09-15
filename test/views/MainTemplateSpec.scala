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

package views

import org.jsoup.nodes.Document
import views.html.IndexView

import scala.jdk.CollectionConverters._

class MainTemplateSpec extends ViewSpecBase {

  private lazy val doc: Document = {
    val application = applicationBuilder().build()
    val view        = application.injector.instanceOf[IndexView]
    val rendered    = asDocument(view.apply()(fakeRequest, messages))
    application.stop()
    rendered
  }

  private def hrefStartingWith(prefix: String): String =
    doc
      .select("a[href]")
      .asScala
      .map(_.attr("href"))
      .find(_.startsWith(prefix))
      .getOrElse(fail(s"no link starting with '$prefix' was rendered on the page"))

  private val sharedPlatUiPages = Seq(
    "http://localhost:12346/accessibility-statement/estates",
    "http://localhost:9250/contact/report-technical-problem",
    "/help/cookies",
    "/help/privacy",
    "/help/terms-and-conditions"
  )

  "MainTemplate" must {

    "render the service navigation component" in {
      assertRenderedByCssSelector(doc, ".govuk-service-navigation")

      doc.getElementsByClass("govuk-service-navigation__service-name").text().trim mustBe messages("service.name")
    }

    sharedPlatUiPages.foreach { page =>
      s"link to $page with the useServiceNavigation parameter" in {
        hrefStartingWith(page) must include("useServiceNavigation")
      }
    }

    "link the service name back to the start of the journey" in {
      hrefStartingWith(frontendAppConfig.loginContinueUrl) mustBe frontendAppConfig.loginContinueUrl
    }

    "offer a sign out link" in {
      hrefStartingWith(controllers.routes.LogoutController.logout().url) mustBe
        controllers.routes.LogoutController.logout().url
    }
  }

}
