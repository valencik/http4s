/*
 * Copyright 2013 http4s.org
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

package org.http4s
package headers

import java.time.{ZoneId, ZonedDateTime}
import scala.concurrent.duration._

class RetryAfterSpec extends HeaderLaws {
  checkAll("Retry-After", headerLaws(`Retry-After`))

  val gmtDate: ZonedDateTime = ZonedDateTime.of(1999, 12, 31, 23, 59, 59, 0, ZoneId.of("GMT"))

  "render" should {
    "format GMT date according to RFC 1123" in {
      `Retry-After`(
        HttpDate.unsafeFromZonedDateTime(
          gmtDate)).renderString must_== "Retry-After: Fri, 31 Dec 1999 23:59:59 GMT"
    }
    "duration in seconds" in {
      `Retry-After`.unsafeFromDuration(120.seconds).renderString must_== "Retry-After: 120"
    }
  }

  "build" should {
    "build correctly for positives" in {
      `Retry-After`.fromLong(0).map(_.value) must beLike { case Right("0") => ok }
    }
    "fail for negatives" in {
      `Retry-After`.fromLong(-10).map(_.value) must beLeft
    }
    "build unsafe for positives" in {
      `Retry-After`.unsafeFromDuration(0.seconds).value must_== "0"
      `Retry-After`.unsafeFromLong(10).value must_== "10"
    }
    "fail unsafe for negatives" in {
      `Retry-After`.unsafeFromDuration(-10.seconds).value must throwA[ParseFailure]
      `Retry-After`.unsafeFromLong(-10).value must throwA[ParseFailure]
    }
  }

  "parse" should {
    "accept http date" in {
      `Retry-After`.parse("Fri, 31 Dec 1999 23:59:59 GMT").map(_.retry) must_== (Right(
        Left(HttpDate.unsafeFromZonedDateTime(gmtDate))))
    }
    "accept duration on seconds" in {
      `Retry-After`.parse("120").map(_.retry) must_== (Right(Right(120L)))
    }
  }
}
