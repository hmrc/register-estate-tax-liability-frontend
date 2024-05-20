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

package services

import java.time.LocalDate
import base.SpecBase
import connectors.EstatesConnector
import models.{CYMinus1TaxYear, CYMinus2TaxYear, CYMinus3TaxYear, CYMinus4TaxYear, TaxLiabilityYear, YearReturnType}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.when
import pages.DidDeclareTaxToHMRCYesNoPage
import play.api.inject.bind
import play.api.test.Helpers._
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.time.TaxYear

import scala.concurrent.Future

class TaxLiabilityServiceSpec extends SpecBase {

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()

  val cyTaxYear: LocalDate = TaxYear.current.starts

  def cyMinus1TaxYear: LocalDate = cyTaxYear.minusYears(1)

  def cyMinus2TaxYear: LocalDate = cyTaxYear.minusYears(2)

  def cyMinus3TaxYear: LocalDate = cyTaxYear.minusYears(3)

  def cyMinus4TaxYear: LocalDate = cyTaxYear.minusYears(4)

  def cyMinus5TaxYear: LocalDate = cyTaxYear.minusYears(5)

  def setCurrentDate(date: LocalDate): LocalDateService = new LocalDateService {
    override def now: LocalDate = date
  }

  def twoDigitYearAsString(date: LocalDate): String = date.getYear.toString.substring(2)

  "getFirstYearOfTaxLiability" must {

    "return the cy minus four tax liability and and true to earlier years" when {

      "the current date is before the december deadline and date of death is more than 4 years ago" in {
        val mockEstatesConnector = Mockito.mock(classOf[EstatesConnector])

        val dateBeforeDec23rd = LocalDate.of(cyTaxYear.getYear, 5, 1)

        val application = applicationBuilder(userAnswers = None)
          .overrides(bind[EstatesConnector].toInstance(mockEstatesConnector))
          .overrides(bind[LocalDateService].toInstance(setCurrentDate(dateBeforeDec23rd)))
          .build()

        val dateOfDeath = LocalDate.of(cyMinus5TaxYear.getYear, 5, 1)

        when(mockEstatesConnector.getDateOfDeath()(any(), any())).thenReturn(Future.successful(dateOfDeath))

        val service = application.injector.instanceOf[TaxLiabilityService]

        val result = service.getFirstYearOfTaxLiability()

        result.futureValue mustEqual TaxLiabilityYear(TaxYear(cyMinus4TaxYear.getYear), earlierYears = true)
      }

      "the current date is on the december deadline and date of death is more than 4 years ago" in {
        val mockEstatesConnector = Mockito.mock(classOf[EstatesConnector])

        val dateBeforeDec23rd = LocalDate.of(cyTaxYear.getYear, 12, 22)

        val application = applicationBuilder(userAnswers = None)
          .overrides(bind[EstatesConnector].toInstance(mockEstatesConnector))
          .overrides(bind[LocalDateService].toInstance(setCurrentDate(dateBeforeDec23rd)))
          .build()

        val dateOfDeath = LocalDate.of(cyMinus5TaxYear.getYear, 5, 1)

        when(mockEstatesConnector.getDateOfDeath()(any(), any())).thenReturn(Future.successful(dateOfDeath))

        val service = application.injector.instanceOf[TaxLiabilityService]

        val result = service.getFirstYearOfTaxLiability()

        result.futureValue mustEqual TaxLiabilityYear(TaxYear(cyMinus4TaxYear.getYear), earlierYears = true)
      }
    }

    "return the cy minus three tax liability and and true to earlier years" when {

      "the current date is after the december deadline and date of death is more than 3 years ago" in {
        val mockEstatesConnector = Mockito.mock(classOf[EstatesConnector])

        val dateAfterDec23rd = LocalDate.of(cyTaxYear.getYear, 12, 23)

        val application = applicationBuilder(userAnswers = None)
          .overrides(bind[EstatesConnector].toInstance(mockEstatesConnector))
          .overrides(bind[LocalDateService].toInstance(setCurrentDate(dateAfterDec23rd)))
          .build()

        val dateOfDeath = LocalDate.of(cyMinus5TaxYear.getYear, 5, 1)

        when(mockEstatesConnector.getDateOfDeath()(any(), any())).thenReturn(Future.successful(dateOfDeath))

        val service = application.injector.instanceOf[TaxLiabilityService]

        val result = service.getFirstYearOfTaxLiability()

        result.futureValue mustEqual TaxLiabilityYear(TaxYear(cyMinus3TaxYear.getYear), earlierYears = true)
      }
    }

    "return the cy minus four tax liability and and false to earlier years" when {
      "the current date is before the december deadline and date of death is 4 years ago" in {
        val mockEstatesConnector = Mockito.mock(classOf[EstatesConnector])

        val dateBeforeDec23rd = LocalDate.of(cyTaxYear.getYear, 5, 1)

        val application = applicationBuilder(userAnswers = None)
          .overrides(bind[EstatesConnector].toInstance(mockEstatesConnector))
          .overrides(bind[LocalDateService].toInstance(setCurrentDate(dateBeforeDec23rd)))
          .build()

        val dateOfDeath = LocalDate.of(cyMinus4TaxYear.getYear, 5, 1)

        when(mockEstatesConnector.getDateOfDeath()(any(), any())).thenReturn(Future.successful(dateOfDeath))

        val service = application.injector.instanceOf[TaxLiabilityService]

        val result = service.getFirstYearOfTaxLiability()

        result.futureValue mustEqual TaxLiabilityYear(TaxYear(cyMinus4TaxYear.getYear), earlierYears = false)
      }
    }

    "return the cy minus three tax liability and and false to earlier years" when {
      "the current date is before the december deadline and date of death is 3 years ago" in {
        val mockEstatesConnector = Mockito.mock(classOf[EstatesConnector])

        val dateBeforeDec23rd = LocalDate.of(cyTaxYear.getYear, 5, 1)

        val application = applicationBuilder(userAnswers = None)
          .overrides(bind[EstatesConnector].toInstance(mockEstatesConnector))
          .overrides(bind[LocalDateService].toInstance(setCurrentDate(dateBeforeDec23rd)))
          .build()

        val dateOfDeath = LocalDate.of(cyMinus3TaxYear.getYear, 5, 1)

        when(mockEstatesConnector.getDateOfDeath()(any(), any())).thenReturn(Future.successful(dateOfDeath))

        val service = application.injector.instanceOf[TaxLiabilityService]

        val result = service.getFirstYearOfTaxLiability()

        result.futureValue mustEqual TaxLiabilityYear(TaxYear(cyMinus3TaxYear.getYear), earlierYears = false)
      }

      "the current date is after the december deadline and date of death is 3 years ago" in {
        val mockEstatesConnector = Mockito.mock(classOf[EstatesConnector])

        val dateBeforeDec23rd = LocalDate.of(cyTaxYear.getYear, 12, 23)

        val application = applicationBuilder(userAnswers = None)
          .overrides(bind[EstatesConnector].toInstance(mockEstatesConnector))
          .overrides(bind[LocalDateService].toInstance(setCurrentDate(dateBeforeDec23rd)))
          .build()

        val dateOfDeath = LocalDate.of(cyMinus3TaxYear.getYear, 5, 1)

        when(mockEstatesConnector.getDateOfDeath()(any(), any())).thenReturn(Future.successful(dateOfDeath))

        val service = application.injector.instanceOf[TaxLiabilityService]

        val result = service.getFirstYearOfTaxLiability()

        result.futureValue mustEqual TaxLiabilityYear(TaxYear(cyMinus3TaxYear.getYear), earlierYears = false)
      }
    }

    "return the cy minus two tax liability and and false to earlier years" when {
      "the current date is before the december deadline and date of death is 2 years ago" in {
        val mockEstatesConnector = Mockito.mock(classOf[EstatesConnector])

        val dateBeforeDec23rd = LocalDate.of(cyTaxYear.getYear, 5, 1)

        val application = applicationBuilder(userAnswers = None)
          .overrides(bind[EstatesConnector].toInstance(mockEstatesConnector))
          .overrides(bind[LocalDateService].toInstance(setCurrentDate(dateBeforeDec23rd)))
          .build()

        val dateOfDeath = LocalDate.of(cyMinus2TaxYear.getYear, 5, 1)

        when(mockEstatesConnector.getDateOfDeath()(any(), any())).thenReturn(Future.successful(dateOfDeath))

        val service = application.injector.instanceOf[TaxLiabilityService]

        val result = service.getFirstYearOfTaxLiability()

        result.futureValue mustEqual TaxLiabilityYear(TaxYear(cyMinus2TaxYear.getYear), earlierYears = false)
      }

      "the current date is after the december deadline and date of death is 2 years ago" in {
        val mockEstatesConnector = Mockito.mock(classOf[EstatesConnector])

        val dateBeforeDec23rd = LocalDate.of(cyTaxYear.getYear, 12, 23)

        val application = applicationBuilder(userAnswers = None)
          .overrides(bind[EstatesConnector].toInstance(mockEstatesConnector))
          .overrides(bind[LocalDateService].toInstance(setCurrentDate(dateBeforeDec23rd)))
          .build()

        val dateOfDeath = LocalDate.of(cyMinus2TaxYear.getYear, 5, 1)

        when(mockEstatesConnector.getDateOfDeath()(any(), any())).thenReturn(Future.successful(dateOfDeath))

        val service = application.injector.instanceOf[TaxLiabilityService]

        val result = service.getFirstYearOfTaxLiability()

        result.futureValue mustEqual TaxLiabilityYear(TaxYear(cyMinus2TaxYear.getYear), earlierYears = false)
      }
    }

    "return the cy minus one tax liability and and false to earlier years" when {
      "the current date is before the december deadline and date of death is 1 years ago" in {
        val mockEstatesConnector = Mockito.mock(classOf[EstatesConnector])

        val dateBeforeDec23rd = LocalDate.of(cyTaxYear.getYear, 5, 1)

        val application = applicationBuilder(userAnswers = None)
          .overrides(bind[EstatesConnector].toInstance(mockEstatesConnector))
          .overrides(bind[LocalDateService].toInstance(setCurrentDate(dateBeforeDec23rd)))
          .build()

        val dateOfDeath = LocalDate.of(cyMinus1TaxYear.getYear, 5, 1)

        when(mockEstatesConnector.getDateOfDeath()(any(), any())).thenReturn(Future.successful(dateOfDeath))

        val service = application.injector.instanceOf[TaxLiabilityService]

        val result = service.getFirstYearOfTaxLiability()

        result.futureValue mustEqual TaxLiabilityYear(TaxYear(cyMinus1TaxYear.getYear), earlierYears = false)
      }

      "the current date is after the december deadline and date of death is 3 years ago" in {
        val mockEstatesConnector = Mockito.mock(classOf[EstatesConnector])

        val dateBeforeDec23rd = LocalDate.of(cyTaxYear.getYear, 12, 23)

        val application = applicationBuilder(userAnswers = None)
          .overrides(bind[EstatesConnector].toInstance(mockEstatesConnector))
          .overrides(bind[LocalDateService].toInstance(setCurrentDate(dateBeforeDec23rd)))
          .build()

        val dateOfDeath = LocalDate.of(cyMinus1TaxYear.getYear, 5, 1)

        when(mockEstatesConnector.getDateOfDeath()(any(), any())).thenReturn(Future.successful(dateOfDeath))

        val service = application.injector.instanceOf[TaxLiabilityService]

        val result = service.getFirstYearOfTaxLiability()

        result.futureValue mustEqual TaxLiabilityYear(TaxYear(cyMinus1TaxYear.getYear), earlierYears = false)
      }
    }
  }

  "getTaxYearOfDeath" must {
    "return the correct tax year the date of death falls in" when {

      "date of death is between Jan 1st and April 5th (inclusive)" in {

      val mockEstatesConnector = Mockito.mock(classOf[EstatesConnector])

      val application = applicationBuilder(userAnswers = None)
        .overrides(bind[EstatesConnector].toInstance(mockEstatesConnector))
        .build()

      val dateOfDeath = LocalDate.of(cyMinus2TaxYear.getYear, 1, 1)

      when(mockEstatesConnector.getDateOfDeath()(any(), any())).thenReturn(Future.successful(dateOfDeath))

      val service = application.injector.instanceOf[TaxLiabilityService]

      val result = service.getTaxYearOfDeath()

      result.futureValue mustEqual TaxYear(cyMinus3TaxYear.getYear)
      }

      "date of death is between April 6th and Dec 31st (inclusive)" in {

        val mockEstatesConnector = Mockito.mock(classOf[EstatesConnector])

        val application = applicationBuilder(userAnswers = None)
          .overrides(bind[EstatesConnector].toInstance(mockEstatesConnector))
          .build()

        val dateOfDeath = LocalDate.of(cyMinus2TaxYear.getYear, 6, 1)

        when(mockEstatesConnector.getDateOfDeath()(any(), any())).thenReturn(Future.successful(dateOfDeath))

        val service = application.injector.instanceOf[TaxLiabilityService]

        val result = service.getTaxYearOfDeath()

        result.futureValue mustEqual TaxYear(cyMinus2TaxYear.getYear)
      }
    }
  }

  ".sendTaxLiability" must {

    "clear out and send tax liability transform to estates when there is a liability" in {
      val mockEstatesConnector = Mockito.mock(classOf[EstatesConnector])

      val application = applicationBuilder(userAnswers = None)
        .overrides(bind[EstatesConnector].toInstance(mockEstatesConnector))
        .build()

      when(mockEstatesConnector.resetTaxLiability()(any(), any()))
        .thenReturn(Future.successful(HttpResponse(OK, "")))

      when(mockEstatesConnector.saveTaxConsequence(any())(any(), any()))
        .thenReturn(Future.successful(HttpResponse(OK, "")))

      val service = application.injector.instanceOf[TaxLiabilityService]

      val userAnswers = emptyUserAnswers
        .set(DidDeclareTaxToHMRCYesNoPage(CYMinus4TaxYear), false).success.value
        .set(DidDeclareTaxToHMRCYesNoPage(CYMinus3TaxYear), false).success.value
        .set(DidDeclareTaxToHMRCYesNoPage(CYMinus2TaxYear), false).success.value
        .set(DidDeclareTaxToHMRCYesNoPage(CYMinus1TaxYear), false).success.value

      val result = service.submitTaxLiability(userAnswers)

      result.futureValue.status mustBe OK
    }

    "clear out tax liability transforms when there is no liability" in {
      val mockEstatesConnector = Mockito.mock(classOf[EstatesConnector])

      val application = applicationBuilder(userAnswers = None)
        .overrides(bind[EstatesConnector].toInstance(mockEstatesConnector))
        .build()

      when(mockEstatesConnector.resetTaxLiability()(any(), any()))
        .thenReturn(Future.successful(HttpResponse(OK, "")))

      val service = application.injector.instanceOf[TaxLiabilityService]

      val userAnswers = emptyUserAnswers
        .set(DidDeclareTaxToHMRCYesNoPage(CYMinus4TaxYear), true).success.value
        .set(DidDeclareTaxToHMRCYesNoPage(CYMinus3TaxYear), true).success.value
        .set(DidDeclareTaxToHMRCYesNoPage(CYMinus2TaxYear), true).success.value
        .set(DidDeclareTaxToHMRCYesNoPage(CYMinus1TaxYear), true).success.value

      val result = service.submitTaxLiability(userAnswers)

      result.futureValue.status mustBe OK
    }
  }

  "evaluate answers to CY-1, CY-2, CY-3 and CY-4" when {

    "need to pay tax for CY-1, CY-2, CY-3, CY-4 years (Before 5 October)" must {

      "generate a list with 4 tax consequences" in {
        val userAnswers = emptyUserAnswers
          .set(DidDeclareTaxToHMRCYesNoPage(CYMinus4TaxYear), false).success.value
          .set(DidDeclareTaxToHMRCYesNoPage(CYMinus3TaxYear), false).success.value
          .set(DidDeclareTaxToHMRCYesNoPage(CYMinus2TaxYear), false).success.value
          .set(DidDeclareTaxToHMRCYesNoPage(CYMinus1TaxYear), false).success.value

        val today = LocalDate.of(cyTaxYear.getYear, 5, 5)

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[LocalDateService].toInstance(setCurrentDate(today)))
          .build()

        val service = application.injector.instanceOf[TaxLiabilityService]

        val expected = service.evaluateTaxYears(userAnswers)

        expected mustBe List(
          YearReturnType(taxReturnYear = twoDigitYearAsString(cyMinus3TaxYear), taxConsequence = true),
          YearReturnType(taxReturnYear = twoDigitYearAsString(cyMinus2TaxYear), taxConsequence = true),
          YearReturnType(taxReturnYear = twoDigitYearAsString(cyMinus1TaxYear), taxConsequence = true),
          YearReturnType(taxReturnYear = twoDigitYearAsString(cyTaxYear), taxConsequence = false)
        )
      }

      "when CY-1 is late (after 5 October)" in {
        val userAnswers = emptyUserAnswers
          .set(DidDeclareTaxToHMRCYesNoPage(CYMinus4TaxYear), false).success.value
          .set(DidDeclareTaxToHMRCYesNoPage(CYMinus3TaxYear), false).success.value
          .set(DidDeclareTaxToHMRCYesNoPage(CYMinus2TaxYear), false).success.value
          .set(DidDeclareTaxToHMRCYesNoPage(CYMinus1TaxYear), false).success.value

        val today = LocalDate.of(cyTaxYear.getYear, 10, 6)

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[LocalDateService].toInstance(setCurrentDate(today)))
          .build()

        val service = application.injector.instanceOf[TaxLiabilityService]

        val expected = service.evaluateTaxYears(userAnswers)

        expected mustBe List(
          YearReturnType(taxReturnYear = twoDigitYearAsString(cyMinus3TaxYear), taxConsequence = true),
          YearReturnType(taxReturnYear = twoDigitYearAsString(cyMinus2TaxYear), taxConsequence = true),
          YearReturnType(taxReturnYear = twoDigitYearAsString(cyMinus1TaxYear), taxConsequence = true),
          YearReturnType(taxReturnYear = twoDigitYearAsString(cyTaxYear), taxConsequence = true)
        )
      }

    }

    "need to pay for some years in CY-1, CY-2, CY-3, CY-4 years (Before 5 October)" must {

      "generate a list with 3 tax consequences where liable" in {
        val userAnswers = emptyUserAnswers
          .set(DidDeclareTaxToHMRCYesNoPage(CYMinus4TaxYear), false).success.value
          .set(DidDeclareTaxToHMRCYesNoPage(CYMinus3TaxYear), true).success.value
          .set(DidDeclareTaxToHMRCYesNoPage(CYMinus2TaxYear), false).success.value
          .set(DidDeclareTaxToHMRCYesNoPage(CYMinus1TaxYear), false).success.value

        val today = LocalDate.of(cyTaxYear.getYear, 5, 5)

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[LocalDateService].toInstance(setCurrentDate(today)))
          .build()

        val service = application.injector.instanceOf[TaxLiabilityService]

        val expected = service.evaluateTaxYears(userAnswers)

        expected mustBe List(
          YearReturnType(taxReturnYear = twoDigitYearAsString(cyMinus3TaxYear), taxConsequence = true),
          YearReturnType(taxReturnYear = twoDigitYearAsString(cyMinus1TaxYear), taxConsequence = true),
          YearReturnType(taxReturnYear = twoDigitYearAsString(cyTaxYear), taxConsequence = false)
        )
      }

    }

    "need to pay tax for CY-1, CY-2 (before 5 October)" when {

      "generate a list with 2 tax consequences" in {
        val userAnswers = emptyUserAnswers
          .set(DidDeclareTaxToHMRCYesNoPage(CYMinus2TaxYear), false).success.value
          .set(DidDeclareTaxToHMRCYesNoPage(CYMinus1TaxYear), false).success.value

        val today = LocalDate.of(cyTaxYear.getYear, 5, 5)

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[LocalDateService].toInstance(setCurrentDate(today)))
          .build()

        val service = application.injector.instanceOf[TaxLiabilityService]

        val expected = service.evaluateTaxYears(userAnswers)

        expected mustBe List(
          YearReturnType(taxReturnYear = twoDigitYearAsString(cyMinus1TaxYear), taxConsequence = true),
          YearReturnType(taxReturnYear = twoDigitYearAsString(cyTaxYear), taxConsequence = false)
        )
      }

    }

    "need to pay tax for CY-2, CY-4" when {

      "generate a list with 2 tax consequences" in {
        val userAnswers = emptyUserAnswers
          .set(DidDeclareTaxToHMRCYesNoPage(CYMinus2TaxYear), false).success.value
          .set(DidDeclareTaxToHMRCYesNoPage(CYMinus4TaxYear), false).success.value

        val today = LocalDate.of(cyTaxYear.getYear, 5, 5)

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[LocalDateService].toInstance(setCurrentDate(today)))
          .build()

        val service = application.injector.instanceOf[TaxLiabilityService]

        val expected = service.evaluateTaxYears(userAnswers)

        expected mustBe List(
          YearReturnType(taxReturnYear = twoDigitYearAsString(cyMinus3TaxYear), taxConsequence = true),
          YearReturnType(taxReturnYear = twoDigitYearAsString(cyMinus1TaxYear), taxConsequence = true)
        )
      }

    }

  }

  "must evaluate answers for CY-1" when {

    "tax owed is late (after 5 October)" must {

      "generate a list with 1 tax consequence (false)" in {
        val userAnswers = emptyUserAnswers
          .set(DidDeclareTaxToHMRCYesNoPage(CYMinus1TaxYear), false).success.value

        val today = LocalDate.of(cyTaxYear.getYear, 10, 6)

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[LocalDateService].toInstance(setCurrentDate(today)))
          .build()

        val service = application.injector.instanceOf[TaxLiabilityService]

        val expected = service.evaluateTaxYears(userAnswers)

        expected mustBe List(
          YearReturnType(taxReturnYear = twoDigitYearAsString(cyTaxYear), taxConsequence = true)
        )
      }

    }

    "tax owed is not late (before 5 October)" must {

      "generate a list with 1 tax consequence (true)" in {
        val userAnswers = emptyUserAnswers
          .set(DidDeclareTaxToHMRCYesNoPage(CYMinus1TaxYear), false).success.value

        val today = LocalDate.of(cyTaxYear.getYear, 10, 5)

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[LocalDateService].toInstance(setCurrentDate(today)))
          .build()

        val service = application.injector.instanceOf[TaxLiabilityService]

        val expected = service.evaluateTaxYears(userAnswers)

        expected mustBe List(
          YearReturnType(taxReturnYear = twoDigitYearAsString(cyTaxYear), taxConsequence = false)
        )
      }

    }

  }

}
