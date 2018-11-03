package com.xuebusi.cms.base.modules.sys.service;

import com.xuebusi.cms.base.modules.sys.dao.OfficeDao;
import com.xuebusi.cms.base.modules.sys.entity.Office;
import com.xuebusi.cms.base.modules.sys.utils.UserUtils;
import com.xuebusi.cms.common.service.TreeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 机构Service
 *
 * @author Idea
 * @version 2014-05-16
 */
@Service
@Transactional(readOnly = true)
public class OfficeService extends TreeService<OfficeDao, Office> {

    public List<Office> findAll() {
        return UserUtils.getOfficeList();
    }

    public List<Office> findList(Boolean isAll) {
        if (isAll != null && isAll) {
            return UserUtils.getOfficeAllList();
        } else {
            return UserUtils.getOfficeList();
        }
    }

    @Transactional(readOnly = true)
    public List<Office> findList(Office office) {
        if (office != null) {
            office.setParentIds(office.getParentIds() + "%");
            return dao.findByParentIdsLike(office);
        }
        return new ArrayList<Office>();
    }

    @Transactional(readOnly = false)
    public void save(Office office) {
        super.save(office);
        UserUtils.removeCache(UserUtils.CACHE_OFFICE_LIST);
    }

    @Transactional(readOnly = false)
    public void delete(Office office) {
        super.delete(office);
        UserUtils.removeCache(UserUtils.CACHE_OFFICE_LIST);
    }

}
