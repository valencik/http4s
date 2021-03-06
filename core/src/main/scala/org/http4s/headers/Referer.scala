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

import org.http4s.util.Writer
import java.nio.charset.StandardCharsets

object Referer extends HeaderKey.Internal[Referer] with HeaderKey.Singleton {
  override def parse(s: String): ParseResult[Referer] =
    ParseResult.fromParser(parser, "Invalid Referer")(s)
  private[http4s] val parser = Uri
    .absoluteUri(StandardCharsets.ISO_8859_1)
    .orElse(Uri.relativeRef(StandardCharsets.ISO_8859_1))
    .map(Referer(_))
}

final case class Referer(uri: Uri) extends Header.Parsed {
  override def key: `Referer`.type = `Referer`
  override def renderValue(writer: Writer): writer.type = uri.render(writer)
}
