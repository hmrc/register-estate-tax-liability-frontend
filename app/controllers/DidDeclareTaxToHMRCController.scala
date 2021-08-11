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

package controllers

import config.annotations.TaxLiability
import controllers.actions.Actions
import forms.YesNoFormProviderWithArguments
import javax.inject.Inject
import models.{Mode, TaxYear, TaxYearRange}
import navigation.Navigator
import pages.DidDeclareTaxToHMRCYesNoPage
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.DidDeclareTaxToHMRCYesNoView

import scala.concurrent.{ExecutionContext, Future}

class DidDeclareTaxToHMRCController @Inject()(
                                               val controllerComponents: MessagesControllerComponents,
                                               @TaxLiability navigator: Navigator,
                                               actions: Actions,
                                               formProvider: YesNoFormProviderWithArguments,
                                               sessionRepository: SessionRepository,
                                               view: DidDeclareTaxToHMRCYesNoView,
                                               taxYearRange: TaxYearRange
                                             )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def form(ranges: Seq[String]) = formProvider.withPrefix("didDeclareToHMRCYesNo", ranges)

  def onPageLoad(mode: Mode, taxYear: TaxYear): Action[AnyContent] = actions.authWithData {
    implicit request =>

      val f = form(Seq(taxYearRange.startYear(taxYear), taxYearRange.endYear(taxYear)))

      val preparedForm = request.userAnswers.get(DidDeclareTaxToHMRCYesNoPage(taxYear)) match {
        case None => f
        case Some(value) => f.fill(value)
      }

      Ok(view(preparedForm, taxYear, taxYearRange.toRange(taxYear), mode))
  }

  def onSubmit(mode: Mode, taxYear: TaxYear): Action[AnyContent] = actions.authWithData.async {
    implicit request =>

      form(Seq(taxYearRange.startYear(taxYear), taxYearRange.endYear(taxYear))).bindFromRequest().fold(
        formWithErrors => {
          Future.successful(BadRequest(view(formWithErrors, taxYear, taxYearRange.toRange(taxYear), mode)))
        },
        value => {
          val page = DidDeclareTaxToHMRCYesNoPage(taxYear)
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(page, value))
            _ <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(page, mode, updatedAnswers))
        }
      )
  }

}
