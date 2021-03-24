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

package org.anvilpowered.falcon

import com.google.inject.Guice
import org.anvilpowered.falcon.discord.JDAUtils
import org.slf4j.LoggerFactory

fun main() {
  val logger = LoggerFactory.getLogger("Falcon")
  val injector = Guice.createInjector(FalconModule(logger))
  val jdaUtils = injector.getInstance(JDAUtils::class.java)
  jdaUtils.setup()
  Runtime.getRuntime().addShutdownHook(Thread {
    jdaUtils.teardown()
  })
}
