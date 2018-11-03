package com.xuebusi.cms.base.modules.sys.dao;

import com.xuebusi.cms.base.modules.sys.entity.Dict;
import com.xuebusi.cms.common.persistence.CrudDao;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 字典DAO接口
 *
 * @author Idea
 * @version 2014-05-16
 */
@Mapper
public interface DictDao extends CrudDao<Dict> {

    List<String> findTypeList(Dict dict);

}
