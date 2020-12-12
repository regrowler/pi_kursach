package utils

import java.sql.Connection

fun Connection.use(block: (connection: Connection) -> Unit) {
    block.invoke(this)
    close()
}