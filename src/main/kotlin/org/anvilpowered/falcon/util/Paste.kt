/*
 *   Falcon - AnvilPowered
 *   Copyright (C) 2020-2021
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anvilpowered.falcon.util

import com.google.inject.Inject
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.util.concurrent.GlobalEventExecutor
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message
import org.asynchttpclient.Dsl.asyncHttpClient
import org.asynchttpclient.Dsl.config
import org.asynchttpclient.filter.FilterContext
import org.asynchttpclient.filter.RequestFilter
import org.slf4j.Logger
import java.awt.Color
import java.net.UnknownHostException
import java.time.OffsetDateTime
import java.time.ZoneOffset

class Paste {

  @Inject
  private lateinit var logger: Logger

  fun post(contents: String, site: String, fileName: String, author: String, message: Message) {
    asyncHttpClient(
      config()
        .setEventLoopGroup(NioEventLoopGroup(4))
        .addRequestFilter(object : RequestFilter {
          override fun <T> filter(ctx: FilterContext<T>): FilterContext<T> {
            return FilterContext.FilterContextBuilder(ctx).request(
              ctx.request.toBuilder()
                .setNameResolver(FalconInetNameResolver(GlobalEventExecutor.INSTANCE))
                .build()
            ).build()
          }
        })
        .setFollowRedirect(true)
        .build()
    ).preparePost(site)
      .setHeader("Content-Type", "text/plain")
      .addHeader("AnvilPowered", "Dump")
      .setBody(contents.toByteArray(Charsets.UTF_8))
      .execute()
      .toCompletableFuture()
      .exceptionally {
        logger.error("An error occurred while uploading attachment!", it)
        null
      }
      .thenApplyAsync { response ->
        try {
          if (response.statusCode != 200) {
            logger.error(
              """
              An error occurred while posting to $site
              The server may be down or there is an issue with your connection!
            """.trimIndent()
            )
            return@thenApplyAsync
          }

          val body = parse(response.getResponseBody(Charsets.UTF_8))
          val key = checkNotNull(body["key"]) { "URL Response missing!" }
          val url = "http://dump.anvilpowered.org/$key.json".replace("\"", "")
          logger.info("New paste created: $url")
          val embed = EmbedBuilder()
            .setColor(Color.CYAN)
            .setTitle(url, url)
            .setDescription("$fileName requested by $author")
            .addField("Important", "Next time use a paste site, please!", true)
            .setTimestamp(OffsetDateTime.now(ZoneOffset.UTC))
            .setFooter("Provided by AnvilPowered")
            .build()
          message.reply(embed).submit()
        } catch (e: UnknownHostException) {
          logger.error("An error occurred!", e)
        } catch (e: SerializationException) {
          logger.error("An error occurred!", e)
        }
      }
  }

  private fun parse(response: String): JsonObject {
    check(response.isNotEmpty()) { "An error occurred while posting the file" }
    return Json.parseToJsonElement(response).jsonObject
  }
}
