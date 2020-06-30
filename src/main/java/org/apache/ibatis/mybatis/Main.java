package org.apache.ibatis.mybatis;


import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @create: 2020-06-16 09:53
 */
public class Main {

  public static void main(String[] args) {
    String resource = "mybatis-config.xml";
    SqlSessionFactory sqlSessionFactory;
    SqlSession sqlSession = null;
    try {
      //该类只有一个方法并且被重载了9次，而且没有任何属性，可见该类唯一的功能就是通过配置文件创建 SqlSessionFactory
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsReader(resource));

      //SqlSession 创建过程
      sqlSession = sqlSessionFactory.openSession();


      //SqlSession 执行过程
      Map<String, Object> param = new HashMap<>();
      param.put("id", 1);
      //我们创建了一个map，并放入了参数。我们钻进去看看
      UserInfo userInfo = sqlSession.selectOne("org.apache.ibatis.mybatis.UserInfoMapper.selectById", param);
      System.out.println(userInfo);


      UserInfoMapper userMapper = sqlSession.getMapper(UserInfoMapper.class);
      UserInfo user = userMapper.selectById(1);
      System.out.println(user);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      sqlSession.close();
    }
  }
}
