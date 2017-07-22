package me.zhouzhuo810.zzapidoc.project.service;

import me.zhouzhuo810.zzapidoc.common.result.BaseResult;
import me.zhouzhuo810.zzapidoc.common.service.BaseService;
import me.zhouzhuo810.zzapidoc.project.entity.DictionaryEntity;

/**
 * Created by admin on 2017/7/22.
 */
public interface DictionaryService extends BaseService<DictionaryEntity> {

    BaseResult addDictionary(String name, String type, String pid, int position, String userId);

    BaseResult getDictionaryByType(String type);

}
