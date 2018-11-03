package com.xuebusi.cms.base.modules.sys.service;

import com.xuebusi.cms.base.modules.sys.dao.DictDao;
import com.xuebusi.cms.base.modules.sys.entity.Dict;
import com.xuebusi.cms.base.modules.sys.utils.DictUtils;
import com.xuebusi.cms.common.service.CrudService;
import com.xuebusi.cms.common.utils.CacheUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 字典Service
 *
 * @author Idea
 * @version 2014-05-16
 */
@Service
@Transactional(readOnly = true)
public class DictService extends CrudService<DictDao, Dict> {

    /**
     * 查询字段类型列表
     *
     * @return
     */
    public List<String> findTypeList() {
        return dao.findTypeList(new Dict());
    }

    @Transactional(readOnly = false)
    public void save(Dict dict) {
        super.save(dict);
        CacheUtils.remove(DictUtils.CACHE_DICT_MAP);
    }

    @Transactional(readOnly = false)
    public void delete(Dict dict) {
        super.delete(dict);
        CacheUtils.remove(DictUtils.CACHE_DICT_MAP);
    }

}
