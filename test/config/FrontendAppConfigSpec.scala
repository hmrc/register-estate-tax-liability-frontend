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

package config

import base.SpecBase
import play.api.i18n.Lang

class FrontendAppConfigSpec extends SpecBase {

  "FrontendAppConfig" must {

    "ask feedback-frontend to render the exit survey with the service navigation component" in {
      frontendAppConfig.feedbackFrontendUrl mustBe
        "http://localhost:9514/feedback/estates?useServiceNavigation"
    }

    "sign out through bas-gateway" in {
      frontendAppConfig.logoutWithBasGatewayUrl mustBe "http://localhost:9553/gg/sign-out"
    }

    "keep the time-out continue URL pointing at this service" in {
      frontendAppConfig.timeOutUrl mustBe
        "http://localhost:8827/register-an-estate/tax-liability/this-service-has-been-reset"
    }

    "expose the sign in URLs" in {
      frontendAppConfig.loginUrl         mustBe "http://localhost:9949/auth-login-stub/gg-sign-in"
      frontendAppConfig.loginContinueUrl mustBe "http://localhost:8827/register-an-estate/tax-liability"
    }

    "expose the registration hub overview URL" in {
      frontendAppConfig.registerEstateHubOverview mustBe
        "http://localhost:8822/register-an-estate/registration-progress"
    }

    "expose the downstream service base URLs" in {
      frontendAppConfig.authUrl         mustBe "http://localhost:8500"
      frontendAppConfig.estatesUrl      mustBe "http://localhost:8832"
      frontendAppConfig.estatesStoreUrl mustBe "http://localhost:8835"
    }

    "expose the timeout dialog and session cache settings" in {
      frontendAppConfig.timeoutLength   mustBe 900
      frontendAppConfig.countdownLength mustBe 120
      frontendAppConfig.cachettl        mustBe 3600L
    }

    "not audit sign out by default" in {
      frontendAppConfig.logoutAudit mustBe false
    }

    "offer both languages" in {
      frontendAppConfig.languageMap mustBe Map("english" -> Lang("en"), "cymraeg" -> Lang("cy"))
    }

    "route the language switch through the LanguageSwitchController" in {
      frontendAppConfig.routeToSwitchLanguage("cymraeg") mustBe
        controllers.routes.LanguageSwitchController.switchToLanguage("cymraeg")
    }
  }

}
