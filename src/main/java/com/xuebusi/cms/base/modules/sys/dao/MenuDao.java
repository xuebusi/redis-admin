package com.xuebusi.cms.base.modules.sys.dao;

import com.xuebusi.cms.base.modules.sys.entity.Menu;
import com.xuebusi.cms.common.persistence.CrudDao;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 菜单DAO接口
 *
 * @author Idea
 * @version 2014-05-16
 */
@Mapper
public interface MenuDao extends CrudDao<Menu> {

    List<Menu> findByParentIdsLike(Menu menu);

    List<Menu> findByUserId(Menu menu);

    int updateParentIds(Menu menu);

    int updateSort(Menu menu);

}
