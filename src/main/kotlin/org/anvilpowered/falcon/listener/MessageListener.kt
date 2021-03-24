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

package org.anvilpowered.falcon.listener

import com.google.common.io.CharStreams
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.anvilpowered.falcon.util.Config
import org.anvilpowered.falcon.util.Paste
import org.slf4j.Logger
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.GZIPInputStream

class MessageListener constructor(
  private val config: Config,
  private val logger: Logger,
  private val paste: Paste,
) : ListenerAdapter() {

  override fun onMessageReceived(event: MessageReceivedEvent) {
    if (event.message.attachments.size != 0
      && config.blockInvites
    ) {
      event.message.delete()
      return
    }
    for (attachment in event.message.attachments) {
      val extension = attachment.fileExtension ?: continue
      var contents = ""
      if (extension.contains("gz")) {
        attachment.downloadToFile().thenApplyAsync {
          try {
            contents = CharStreams.toString(InputStreamReader(GZIPInputStream(FileInputStream(it))))
          } catch (e: Exception) {
            logger.error("An error occurred decompressing a file!", e)
          }
          deleteFile(it.toPath())
          paste.post(contents, "http://dump.anvilpowered.org/dump", attachment.fileName, event.author.name, event.message)
          return@thenApplyAsync
        }
      }
      event.channel.sendTyping().submit()
      if (extension.equals("log", true)
        || extension.equals("txt", true)
        || extension.equals("conf", true)
        || extension.equals("hocon", true)
        || extension.equals("toml", true)
        || extension.equals("yaml", true)
        || extension.equals("debug", true)
      ) {
        attachment.retrieveInputStream().thenAcceptAsync {
          contents = CharStreams.toString(InputStreamReader(it))
          paste.post(contents, "http://dump.anvilpowered.org/dump", attachment.fileName, event.author.name, event.message)
          return@thenAcceptAsync
        }
      }
      event.channel.sendMessage("An error occurred while uploading your file to AnvilPowered servers.")
    }
  }

  private fun deleteFile(path: Path) {
    GlobalScope.async {
      delay(10000)
      Files.delete(path)
    }
  }
}