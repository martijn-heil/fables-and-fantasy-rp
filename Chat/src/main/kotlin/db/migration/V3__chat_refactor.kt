package db.migration

import com.fablesfantasyrp.plugin.chat.channel.*
import org.flywaydb.core.api.migration.jdbc.BaseJdbcMigration
import java.sql.Connection
import java.util.*

class V3__chat_refactor : BaseJdbcMigration() {
	override fun migrate(connection: Connection) {
		connection.prepareStatement("ALTER TABLE fables_players ADD new_chat_channel JAVA_OBJECT").executeUpdate()

		val stmnt = connection.prepareStatement("SELECT id, chat_channel FROM fables_players")
		val result = stmnt.executeQuery()

		while (result.next()) {
			val uuid = result.getObject("id", UUID::class.java)
			val channel = chatChannelFromString(result.getString("chat_channel")) ?: ChatOutOfCharacter

			val stmnt2 = connection.prepareStatement("UPDATE fables_players SET chat_channel = ? WHERE id = ?")

			stmnt2.setObject(1, channel)
			stmnt2.setObject(2, uuid)
			stmnt2.executeUpdate()
		}

		connection.prepareStatement("ALTER TABLE fables_players DROP COLUMN chat_channel").executeUpdate()
		connection.prepareStatement("ALTER TABLE fables_players RENAME COLUMN new_chat_channel TO chat_channel").executeUpdate()
	}

	private fun chatChannelFromString(s: String): ChatChannel? = when (s.lowercase()) {
				"ooc" -> ChatOutOfCharacter
				"looc" -> ChatLocalOutOfCharacter
				"ic" -> ChatInCharacter
				"ic.whisper" -> ChatInCharacterWhisper
				"ic.quiet" -> ChatInCharacterQuiet
				"ic.shout" -> ChatInCharacterShout
				"staff" -> ChatStaff
				"spectator" -> ChatSpectator
				else -> null
			}
}
