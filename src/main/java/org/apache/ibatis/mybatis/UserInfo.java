package org.apache.ibatis.mybatis;

/**
 * @create: 2020-06-16 09:55
 */
public class UserInfo {
  private int id;
  private String username;
  private String realname;

  @Override
  public String toString() {
    return "UserInfo{" +
      "id='" + id + '\'' +
      ", username='" + username + '\'' +
      ", realname='" + realname + '\'' +
      '}';
  }
}
