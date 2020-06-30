package org.apache.ibatis.mybatis;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author 阿赫瓦里
 * @ClassName: OrdersMapperCustom
 * @Description: TODO(OrdersMapperCustom的mapper)
 */
@Mapper
public interface OrdersCustomMapper {
  /**
   * 查询订单，关联查询用户信息
   */
  public List<OrdersCustom> findOrdersUser();

  /**
   * 查询订单关联查询用户信息，使用reslutMap实现
   */
  public List<Orders> findOrdersUserResultMap();
}
