package org.apache.ibatis.mybatis;

/**
 * @ClassName: OrdersCustom
 * @Description: TODO(订单的扩展类,通过此类映射订单和用户的查询结果,让此类继承字段较多的实体类)
 * @author: 阿赫瓦里
 */
public class OrdersCustom extends Orders {
    // 添加用户的属性
    private String username;
    private String sex;
    private String address;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getSex() {
    return sex;
  }

  public void setSex(String sex) {
    this.sex = sex;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }
}
