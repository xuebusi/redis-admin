package com.xuebusi.cms.base.modules.sys.service;

import com.xuebusi.cms.base.modules.sys.dao.AreaDao;
import com.xuebusi.cms.base.modules.sys.entity.Area;
import com.xuebusi.cms.base.modules.sys.utils.UserUtils;
import com.xuebusi.cms.common.service.TreeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 区域Service
 *
 * @author Idea
 * @version 2014-05-16
 */
@Service
@Transactional(readOnly = true)
public class AreaService extends TreeService<AreaDao, Area> {

    public List<Area> findAll() {
        return UserUtils.getAreaList();
    }

    @Transactional(readOnly = false)
    public void save(Area area) {
        super.save(area);
        UserUtils.removeCache(UserUtils.CACHE_AREA_LIST);
    }

    @Transactional(readOnly = false)
    public void delete(Area area) {
        super.delete(area);
        UserUtils.removeCache(UserUtils.CACHE_AREA_LIST);
    }

}
