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

package repositories

import base.SpecBase
import config.FrontendAppConfig
import models.UserAnswers
import org.mongodb.scala.model.Filters
import org.scalatest.BeforeAndAfterEach
import pages.CYMinusOneYesNoPage
import play.api.libs.json.Json
import uk.gov.hmrc.mongo.test.MongoSupport

import java.time.Instant
import java.time.temporal.ChronoUnit
import scala.concurrent.ExecutionContext.Implicits.global

class SessionRepositorySpec extends SpecBase with MongoSupport with BeforeAndAfterEach {

  private lazy val config: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]

  private lazy val repository = new DefaultSessionRepository(mongoComponent, config)

  override def beforeEach(): Unit = {
    super.beforeEach()
    repository.collection.drop().toFuture().futureValue
    repository.ensureIndexes().futureValue
  }

  private val stale = Instant.now().minus(1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.MILLIS)

  private def storedAnswers: UserAnswers =
    repository.collection.find(Filters.equal("_id", userAnswersId)).headOption().futureValue.value

  "SessionRepository" must {

    "expire cached answers using the configured time to live" in {

      val index = repository.indexes.head

      index.getOptions.getName                                               mustBe "user-answers-last-updated-index"
      index.getOptions.getExpireAfter(java.util.concurrent.TimeUnit.SECONDS) mustBe config.cachettl
    }

    "store answers and read them back" in {

      val answers = UserAnswers(userAnswersId, Json.obj("cyMinusOneYesNo" -> true))

      repository.set(answers).futureValue mustBe true

      val stored = repository.get(userAnswersId).futureValue.value

      stored.id                       mustBe userAnswersId
      stored.get(CYMinusOneYesNoPage) mustBe Some(true)
    }

    "overwrite the answers already held for a session" in {

      repository.set(UserAnswers(userAnswersId, Json.obj("cyMinusOneYesNo" -> true))).futureValue
      repository.set(UserAnswers(userAnswersId, Json.obj("cyMinusOneYesNo" -> false))).futureValue

      repository.collection.countDocuments().toFuture().futureValue mustBe 1

      repository.get(userAnswersId).futureValue.value.get(CYMinusOneYesNoPage) mustBe Some(false)
    }

    "stamp the time the answers were written, so they expire a fixed time after the last change" in {

      val before = Instant.now().truncatedTo(ChronoUnit.MILLIS)

      repository.set(UserAnswers(userAnswersId, Json.obj(), stale)).futureValue

      storedAnswers.lastUpdated.isBefore(before) mustBe false
    }

    "keep the session alive by touching lastUpdated whenever the answers are read" in {

      repository.collection.insertOne(UserAnswers(userAnswersId, Json.obj(), stale)).toFuture().futureValue

      // findOneAndUpdate hands back the document as it was, so the touch shows up on the next read
      repository.get(userAnswersId).futureValue.value.lastUpdated mustBe stale

      storedAnswers.lastUpdated.isAfter(stale) mustBe true
    }

    "read nothing for a session that has no answers" in {
      repository.get("some-other-session").futureValue mustBe None
    }

    "forget a session when its cache is reset" in {

      repository.set(UserAnswers(userAnswersId)).futureValue

      repository.resetCache(userAnswersId).futureValue mustBe Some(true)

      repository.get(userAnswersId).futureValue mustBe None
    }
  }

}
