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

package controllers

import config.annotations.TaxLiability
import controllers.actions.Actions
import forms.YesNoFormProviderWithArguments
import javax.inject.Inject
import models.{CYMinus1TaxYear, Mode, TaxYearRange}
import navigation.Navigator
import pages.CYMinusOneYesNoPage
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.CYMinusOneYesNoView

import scala.concurrent.{ExecutionContext, Future}

class CYMinusOneLiabilityController @Inject()(
                                 val controllerComponents: MessagesControllerComponents,
                                 @TaxLiability navigator: Navigator,
                                 actions: Actions,
                                 formProvider: YesNoFormProviderWithArguments,
                                 sessionRepository: SessionRepository,
                                 view: CYMinusOneYesNoView,
                                 taxYearRange: TaxYearRange
                               )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def form(ranges: Seq[String]) = formProvider.withPrefix("cyMinusOneYesNo.liability", ranges)

  private val workingTaxYear = CYMinus1TaxYear
  def onPageLoad(mode: Mode): Action[AnyContent] = actions.authWithData {
    implicit request =>

      val f = form(Seq(taxYearRange.startYear(workingTaxYear), taxYearRange.endYear(workingTaxYear)))

      val preparedForm = request.userAnswers.get(CYMinusOneYesNoPage) match {
        case None => f
        case Some(value) => f.fill(value)
      }

      Ok(view(preparedForm, taxYearRange.toRange(workingTaxYear), mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = actions.authWithData.async {
    implicit request =>

      val f = form(Seq(taxYearRange.startYear(workingTaxYear), taxYearRange.endYear(workingTaxYear)))

      f.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, taxYearRange.toRange(workingTaxYear), mode))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(CYMinusOneYesNoPage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(CYMinusOneYesNoPage, mode, updatedAnswers))
      )
  }
}
