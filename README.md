# H2 1.4 "Lob not found" Issue #

Reproducible failing unit test for the [H2 1.4 "Lob not found" issue][google-groups].


## Howto ##

Prerequisites:
  - Java 8

On Linux:

```
# ./gradlew --info build
```

On Windows:

```
# gradlew --info build
```

### The stacktrace ###

The `H2LobIssueTest.testConcurrentLobModification` fails due to the `Lob not found` exception in H2.

On Travis: [![Build Status](https://travis-ci.org/bwaldvogel/h2-lob-issue.png?branch=master)](https://travis-ci.org/bwaldvogel/h2-lob-issue)


```
Caused by:
org.hibernate.exception.GenericJDBCException: could not load an entity: [de.bwaldvogel.LobEntity#1]
	at org.hibernate.exception.SQLStateConverter.handledNonSpecificException(SQLStateConverter.java:140)
	at org.hibernate.exception.SQLStateConverter.convert(SQLStateConverter.java:128)
	at org.hibernate.exception.JDBCExceptionHelper.convert(JDBCExceptionHelper.java:66)
	at org.hibernate.loader.Loader.loadEntity(Loader.java:1937)
	at org.hibernate.loader.entity.AbstractEntityLoader.load(AbstractEntityLoader.java:86)
	at org.hibernate.loader.entity.AbstractEntityLoader.load(AbstractEntityLoader.java:76)
	at org.hibernate.persister.entity.AbstractEntityPersister.load(AbstractEntityPersister.java:3270)
	at org.hibernate.event.def.DefaultLoadEventListener.loadFromDatasource(DefaultLoadEventListener.java:496)
	at org.hibernate.event.def.DefaultLoadEventListener.doLoad(DefaultLoadEventListener.java:477)
	at org.hibernate.event.def.DefaultLoadEventListener.load(DefaultLoadEventListener.java:227)
	at org.hibernate.event.def.DefaultLoadEventListener.proxyOrLoad(DefaultLoadEventListener.java:285)
	at org.hibernate.event.def.DefaultLoadEventListener.onLoad(DefaultLoadEventListener.java:152)
	at org.hibernate.impl.SessionImpl.fireLoad(SessionImpl.java:1080)
	at org.hibernate.impl.SessionImpl.get(SessionImpl.java:997)
	at org.hibernate.impl.SessionImpl.get(SessionImpl.java:990)
	at org.hibernate.ejb.AbstractEntityManagerImpl.find(AbstractEntityManagerImpl.java:610)
	... 13 more

	Caused by:
	org.h2.jdbc.JdbcSQLException: General error: "java.lang.RuntimeException: Lob not found: 25" [50000-182]
		at org.h2.message.DbException.getJdbcSQLException(DbException.java:345)
		at org.h2.message.DbException.get(DbException.java:168)
		at org.h2.message.DbException.convert(DbException.java:295)
		at org.h2.message.DbException.toSQLException(DbException.java:268)
		at org.h2.message.TraceObject.logAndConvert(TraceObject.java:352)
		at org.h2.jdbc.JdbcBlob.getBytes(JdbcBlob.java:101)
		at org.hibernate.type.SerializableToBlobType.get(SerializableToBlobType.java:82)
		at org.hibernate.type.AbstractLobType.nullSafeGet(AbstractLobType.java:68)
		at org.hibernate.type.AbstractType.hydrate(AbstractType.java:105)
		at org.hibernate.persister.entity.AbstractEntityPersister.hydrate(AbstractEntityPersister.java:2267)
		at org.hibernate.loader.Loader.loadFromResultSet(Loader.java:1423)
		at org.hibernate.loader.Loader.instanceNotYetLoaded(Loader.java:1351)
		at org.hibernate.loader.Loader.getRow(Loader.java:1251)
		at org.hibernate.loader.Loader.getRowFromResultSet(Loader.java:619)
		at org.hibernate.loader.Loader.doQuery(Loader.java:745)
		at org.hibernate.loader.Loader.doQueryAndInitializeNonLazyCollections(Loader.java:270)
		at org.hibernate.loader.Loader.loadEntity(Loader.java:1933)
		... 25 more

		Caused by:
		java.lang.RuntimeException: Lob not found: 25
			at org.h2.message.DbException.throwInternalError(DbException.java:242)
			at org.h2.store.LobStorageMap.getInputStream(LobStorageMap.java:236)
			at org.h2.value.ValueLobDb.getInputStream(ValueLobDb.java:392)
			at org.h2.jdbc.JdbcBlob.getBytes(JdbcBlob.java:92)
			... 36 more
```


### What is the test doing? ###

The unit test (`H2LobIssueTest`) does the following a couple of times in multiple threads:
  - Load the entity with a BLOB
  - Append data to the BLOB
  - Write changes back to the database


### H2 configuration ###

The H2 database is configured in `TestConfig.dataSource` with the following JDBC URL: `jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false`.

[google-groups]: https://groups.google.com/forum/#!topic/h2-database/t0Bg5paQZ1U
