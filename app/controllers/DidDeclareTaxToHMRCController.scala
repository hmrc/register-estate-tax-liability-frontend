/*
 * Copyright 2020 HM Revenue & Customs
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
import forms.YesNoFormProvider
import javax.inject.Inject
import models.{Mode, TaxYear, TaxYearRange}
import navigation.Navigator
import pages.DidDeclareTaxToHMRCYesNoPage
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.DidDeclareTaxToHMRCYesNoView

import scala.concurrent.{ExecutionContext, Future}

class DidDeclareTaxToHMRCController @Inject()(
                                               val controllerComponents: MessagesControllerComponents,
                                               @TaxLiability navigator: Navigator,
                                               actions: Actions,
                                               formProvider: YesNoFormProvider,
                                               sessionRepository: SessionRepository,
                                               view: DidDeclareTaxToHMRCYesNoView
                                             )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider.withPrefix("didDeclareToHMRC")

  def onPageLoad(mode: Mode, taxYear: TaxYear): Action[AnyContent] = actions.authWithData {
    implicit request =>
      val range = TaxYearRange(taxYear)

      val preparedForm = request.userAnswers.get(DidDeclareTaxToHMRCYesNoPage(taxYear)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, taxYear, range.toRange, mode))
  }

  def onSubmit(mode: Mode, taxYear: TaxYear): Action[AnyContent] = actions.authWithData.async {
    implicit request =>
      form.bindFromRequest().fold(
        formWithErrors => {
          val range = TaxYearRange(taxYear)
          Future.successful(BadRequest(view(formWithErrors, taxYear, range.toRange, mode)))
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