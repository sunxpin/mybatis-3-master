package org.apache.ibatis.mybatis;

import org.apache.ibatis.annotations.Mapper;

/**
 * @create: 2020-06-16 09:55
 */
@Mapper
public interface UserInfoMapper {
  UserInfo selectById(int id);
}
