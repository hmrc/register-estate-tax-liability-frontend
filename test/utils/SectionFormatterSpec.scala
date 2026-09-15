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

package utils

import base.SpecBase
import play.twirl.api.Html
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import viewmodels.{AnswerRow, AnswerSection, RepeaterAnswerRow, RepeaterAnswerSection}

class SectionFormatterSpec extends SpecBase {

  private val firstRow  = AnswerRow(label = "site.yes", answer = Html("Yes"), changeUrl = "/change-first")
  private val secondRow = AnswerRow(label = "site.no", answer = Html("No"), changeUrl = "/change-second")

  "SectionFormatter" when {

    "given answer sections" must {

      "turn every row into a summary list row" in {

        val result = SectionFormatter.formatSections(Seq(AnswerSection(None, Seq(firstRow, secondRow))))

        result.size mustBe 2

        result.head.key.content   mustBe Text(messages("site.yes"))
        result.head.key.classes   mustBe "govuk-!-width-two-thirds"
        result.head.value.content mustBe HtmlContent(Html("Yes"))

        result.last.value.content mustBe HtmlContent(Html("No"))
      }

      "give each row a change link labelled with its own question" in {

        val result = SectionFormatter.formatSections(Seq(AnswerSection(None, Seq(firstRow, secondRow))))

        val actions = result.flatMap(_.actions.toSeq).flatMap(_.items)

        actions.map(_.href)               mustBe Seq("/change-first", "/change-second")
        actions.map(_.classes)            mustBe Seq("change-link-0", "change-link-1")
        actions.map(_.visuallyHiddenText) mustBe Seq(Some(messages("site.yes")), Some(messages("site.no")))
        actions.map(_.content)            mustBe Seq.fill(2)(Text(messages("site.edit")))
      }

      "flatten multiple sections into a single list, numbering the change links per section" in {

        val result = SectionFormatter.formatSections(
          Seq(AnswerSection(None, Seq(firstRow)), AnswerSection(None, Seq(secondRow)))
        )

        result.size mustBe 2

        result.flatMap(_.actions.toSeq).flatMap(_.items).map(_.classes) mustBe
          Seq("change-link-0", "change-link-0")
      }

      "return nothing when there is nothing to show" in {
        SectionFormatter.formatSections(Nil)                           mustBe Nil
        SectionFormatter.formatSections(Seq(AnswerSection(None, Nil))) mustBe Nil
      }
    }

    "given a repeater section" must {

      "fail, because the estates tax liability journey has no repeated answers" in {

        val repeater = RepeaterAnswerSection(
          headingKey = "heading",
          relevanceRow = firstRow,
          rows = Seq(RepeaterAnswerRow("answer", "/change", "/delete")),
          addLinkKey = "add",
          addLinkUrl = "/add"
        )

        intercept[NotImplementedError] {
          SectionFormatter.formatSections(Seq(repeater))
        }
      }
    }
  }

}
