package com.fablesfantasyrp.plugin.database.testfixtures

abstract class BaseH2RepositoryTest(defaultSchema: String) {
	protected val dataSource = createTestDataSource(defaultSchema)
}
