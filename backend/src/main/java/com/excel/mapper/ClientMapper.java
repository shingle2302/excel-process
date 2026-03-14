package com.excel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.excel.entity.Client;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ClientMapper extends BaseMapper<Client> {
}