package com.mybank

import io.vertx.core.AbstractVerticle
import io.vertx.core.Vertx
import io.vertx.kotlin.mysqlclient.mySQLConnectOptionsOf
import io.vertx.kotlin.sqlclient.poolOptionsOf
import io.vertx.mysqlclient.MySQLClient
import io.vertx.mysqlclient.MySQLPool

internal class BaseVerticle : AbstractVerticle() {
	override fun start() {

		var connectOptions = mySQLConnectOptionsOf(database = "mydb",
				host = "127.0.0.1",
				password = "password1!", port = 6603,
				user = "root")

		var poolOptions = poolOptionsOf(maxSize = 5)

		var client = MySQLPool.pool(vertx, connectOptions, poolOptions)


		client.query("INSERT INTO mytable('id','name') VALUES (2,'Jimis2')").execute { insert ->
			if (insert.succeeded()) {

				var rows = insert.result()
				var lastInsertId = rows.property(MySQLClient.LAST_INSERTED_ID)
				println("Last inserted id is: ${lastInsertId}")

				client.query("SELECT * FROM mytable WHERE id=1").execute { select ->
					if (select.succeeded()) {
						var result = select.result()
						println("Got ${result.size()} rows ")
					} else {
						select.cause().printStackTrace()
					}

					client.close()
				}

			} else {
				insert.cause().printStackTrace()
				client.close()
			}
		}

//		client.query("INSERT INTO mytable('id','name') VALUES ('2','Jimis2')").execute { ar ->
//			if (ar.succeeded()) {
//				var rows = ar.result()
//				var lastInsertId = rows.property(MySQLClient.LAST_INSERTED_ID)
//				println("Last inserted id is: ${lastInsertId}")
//			} else {
//				println("Failure: ${ar.cause().printStackTrace()}")
//			}
//		}
//
//		client.query("SELECT * FROM mytable WHERE id=1").execute { ar ->
//			if (ar.succeeded()) {
//				var result = ar.result()
//				println("Got ${result.size()} rows ")
//			} else {
//				println("Failure: ${ar.cause().stackTrace}")
//			}
//
//			// Now close the pool
//			client.close()
//		}

	}
	override fun stop() {
		println("BasicVerticle stopped")
	}
}

val vertx = Vertx.vertx()

fun main() {
	val vertx = Vertx.vertx()
	vertx.deployVerticle(BaseVerticle())
	//vertx.deployVerticle(BaseVerticle()).onFailure { it.printStackTrace() }
}
