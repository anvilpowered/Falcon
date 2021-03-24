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

package org.anvilpowered.falcon.discord

import com.google.inject.Inject
import com.google.inject.Singleton
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.internal.entities.EntityBuilder
import org.anvilpowered.falcon.listener.MessageListener
import org.anvilpowered.falcon.util.Config
import org.anvilpowered.falcon.util.Paste
import org.slf4j.Logger

@Singleton
class JDAUtils @Inject constructor(
  private val config: Config,
  private val logger: Logger,
  private val paste: Paste,
) {

  lateinit var bot: JDA

  fun setup() {
    bot = JDABuilder.createDefault(config.botToken)
      .addEventListeners(MessageListener(config, logger, paste))
      .build()
    bot.isAutoReconnect = true
    bot.presence.activity = EntityBuilder.createActivity("Rule Breakers", null, Activity.ActivityType.WATCHING)
  }

  fun teardown() = bot.shutdownNow()
}
