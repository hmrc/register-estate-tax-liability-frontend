package controllers

import base.SpecBase
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.IndexView

class IndexControllerSpec extends SpecBase {

  "Index Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, routes.CYMinusFourEarlierYearsLiabilityController.onPageLoad().url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[IndexView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view()(fakeRequest, messages).toString

      application.stop()
    }
  }
}
