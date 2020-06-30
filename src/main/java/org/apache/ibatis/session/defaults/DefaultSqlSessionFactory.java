/**
 *    Copyright 2009-2019 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.session.defaults;

import org.apache.ibatis.exceptions.ExceptionFactory;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.*;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.managed.ManagedTransactionFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Clinton Begin
 */
public class DefaultSqlSessionFactory implements SqlSessionFactory {

  private final Configuration configuration;

  public DefaultSqlSessionFactory(Configuration configuration) {
    this.configuration = configuration;
  }

  @Override
  public SqlSession openSession() {
    return openSessionFromDataSource(configuration.getDefaultExecutorType(), null, false);
  }

  @Override
  public SqlSession openSession(boolean autoCommit) {
    return openSessionFromDataSource(configuration.getDefaultExecutorType(), null, autoCommit);
  }

  @Override
  public SqlSession openSession(ExecutorType execType) {
    return openSessionFromDataSource(execType, null, false);
  }

  @Override
  public SqlSession openSession(TransactionIsolationLevel level) {
    return openSessionFromDataSource(configuration.getDefaultExecutorType(), level, false);
  }

  @Override
  public SqlSession openSession(ExecutorType execType, TransactionIsolationLevel level) {
    return openSessionFromDataSource(execType, level, false);
  }

  @Override
  public SqlSession openSession(ExecutorType execType, boolean autoCommit) {
    return openSessionFromDataSource(execType, null, autoCommit);
  }

  @Override
  public SqlSession openSession(Connection connection) {
    return openSessionFromConnection(configuration.getDefaultExecutorType(), connection);
  }

  @Override
  public SqlSession openSession(ExecutorType execType, Connection connection) {
    return openSessionFromConnection(execType, connection);
  }

  @Override
  public Configuration getConfiguration() {
    return configuration;
  }

  private SqlSession openSessionFromDataSource(ExecutorType execType, TransactionIsolationLevel level, boolean autoCommit) {
    Transaction tx = null;
    try {
      // 获取配置文件中的环境，也就是我们配置的 <environments default="development">标签
      final Environment environment = configuration.getEnvironment();

      /**
       * <p>
       *  然后根据环境对象获取事务工厂，如果配置文件中没有配置，则创建一个 ManagedTransactionFactory 对象直接返回。
       *  否则调用环境对象的 getTransactionFactory 方法，该方法和我们配置的一样返回了一个 JdbcTransactionFactory(<transactionManager type="JDBC"/>)，
       *  而实际上，TransactionFactory 只有2个实现类，一个是 ManagedTransactionFactory ，一个是 JdbcTransactionFactory。
       *  </p>
       */
      final TransactionFactory transactionFactory = getTransactionFactoryFromEnvironment(environment);

      /**
       *  获取了 JdbcTransactionFactory 后，调用 JdbcTransactionFactory 的 newTransaction 方法创建一个事务对象，参数是数据源，level 是null， 自动提交还是false。
       *
       *  <p>
       *    newTransaction 创建了一个 JdbcTransaction 对象，我们看看该类的构造：
       *    可以看到，该类都是有关连接和事务的方法，比如commit，openConnection，rollback，和JDBC 的connection 功能很相似。
       *    而我们刚刚看到的level是什么呢?在源码中我们看到了答案:就是 “事务的隔离级别”。
       *    并且该事务对象(JdbcTransaction)还包含了JDBC 的Connection 对象和 DataSource 数据源对象，好亲切啊，可见这个事务对象就是JDBC的事务的封装。
       *  </p>
       *
       */
      tx = transactionFactory.newTransaction(environment.getDataSource(), level, autoCommit);

      /**
       *上一步已经创建好事务对象。接下来将事务对象执行器作为参数执行 configuration 的 newExecutor 方法来获取一个 执行器类，我们看看该方法实现：
       *
       *<p>
       *   首先，该方法判断给定的执行类型是否为null，如果为null，则使用默认的执行器， 也就是 ExecutorType.SIMPLE，然后根据执行的类型来创建不同的执行器，
       *   默认是 SimpleExecutor 执行器，这里楼主需要解释以下执行器：
       *
       *      Mybatis有三种基本的Executor执行器，SimpleExecutor、ReuseExecutor、BatchExecutor。
       *      SimpleExecutor：每执行一次update或select，就开启一个Statement对象，用完立刻关闭Statement对象。
       *
       *      ReuseExecutor：执行update或select，以sql作为key查找Statement对象，存在就使用，不存在就创建，用完后，
       *      不关闭Statement对象，而是放置于Map<String, Statement>内，供下一次使用。简言之，就是重复使用Statement对象。
       *
       *      BatchExecutor：执行update（没有select，JDBC批处理不支持select），将所有sql都添加到批处理中（addBatch()），
       *      等待统一执行（executeBatch()），它缓存了多个Statement对象，每个Statement对象都是addBatch()完毕后，等待逐一执行executeBatch()批处理。与JDBC批处理相同。
       *
       *      作用范围：Executor的这些特点，都严格限制在SqlSession生命周期范围内。
       *
       *</p>
       *
       * <p>
       *   我们再看看默认执行器的构造方法，
       *
       *
       * </p>
       *
       */
      final Executor executor = configuration.newExecutor(tx, execType);

      /**
       * <p>
       *    此时我们已经有了执行器，此时创建 DefaultSqlSession 对象，携带 configuration, executor, autoCommit 三个参数，该构造器就是简单的赋值过程。
       *    我们有必要看看该类的结构：该类包含了常用的所有方法，包括事务方法，可以说，该类封装了执行器和事务类。而执行器才是具体的执行工作人员。
       *    至此，我们已经完成了 SqlSession 的创建过程。
       *
       * </p>
       */
      return new DefaultSqlSession(configuration, executor, autoCommit);
    } catch (Exception e) {
      closeTransaction(tx); // may have fetched a connection so lets call close()
      throw ExceptionFactory.wrapException("Error opening session.  Cause: " + e, e);
    } finally {
      ErrorContext.instance().reset();
    }
  }

  private SqlSession openSessionFromConnection(ExecutorType execType, Connection connection) {
    try {
      boolean autoCommit;
      try {
        autoCommit = connection.getAutoCommit();
      } catch (SQLException e) {
        // Failover to true, as most poor drivers
        // or databases won't support transactions
        autoCommit = true;
      }
      final Environment environment = configuration.getEnvironment();
      final TransactionFactory transactionFactory = getTransactionFactoryFromEnvironment(environment);
      final Transaction tx = transactionFactory.newTransaction(connection);
      final Executor executor = configuration.newExecutor(tx, execType);
      return new DefaultSqlSession(configuration, executor, autoCommit);
    } catch (Exception e) {
      throw ExceptionFactory.wrapException("Error opening session.  Cause: " + e, e);
    } finally {
      ErrorContext.instance().reset();
    }
  }

  private TransactionFactory getTransactionFactoryFromEnvironment(Environment environment) {
    if (environment == null || environment.getTransactionFactory() == null) {
      return new ManagedTransactionFactory();
    }
    return environment.getTransactionFactory();
  }

  private void closeTransaction(Transaction tx) {
    if (tx != null) {
      try {
        tx.close();
      } catch (SQLException ignore) {
        // Intentionally ignore. Prefer previous error.
      }
    }
  }

}
