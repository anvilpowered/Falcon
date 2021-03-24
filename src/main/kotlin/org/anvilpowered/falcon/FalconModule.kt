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

import com.google.inject.AbstractModule
import org.anvilpowered.falcon.util.Config
import org.slf4j.Logger
import org.spongepowered.configurate.hocon.HoconConfigurationLoader
import java.nio.file.Paths

class FalconModule(
  private val logger: Logger,
) : AbstractModule() {

  override fun configure() {
    super.configure()
    bind(Logger::class.java).toInstance(logger)
    val loader = HoconConfigurationLoader.builder().path(Paths.get("./falcon.conf")).build()
    val rootNode = loader.load()
    val config: Config
    if (rootNode.empty()) {
      rootNode.set(Config().apply { config = this })
      loader.save(rootNode)
    } else {
      config = rootNode[Config::class.java]!!
    }
    bind(Config::class.java).toInstance(config)
  }
}
