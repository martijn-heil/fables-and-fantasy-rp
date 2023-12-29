package com.fablesfantasyrp.plugin.database

abstract class BaseH2RepositoryTest(defaultSchema: String) {
	protected val dataSource = createTestDataSource(defaultSchema)
}
