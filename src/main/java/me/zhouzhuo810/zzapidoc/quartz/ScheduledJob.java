package me.zhouzhuo810.zzapidoc.quartz;

import me.zhouzhuo810.zzapidoc.cache.entity.CacheEntity;
import me.zhouzhuo810.zzapidoc.cache.service.CacheService;
import me.zhouzhuo810.zzapidoc.common.utils.FileUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 计划任务
 */
@Component("ScheduledJob")
public class ScheduledJob {

    @Resource(name = "cacheServiceImpl")
    CacheService mCacheService;

    public void jobTask() {
        List<CacheEntity> all = mCacheService.getAll();
        if (all != null && all.size() > 0) {
            for (CacheEntity cacheEntity : all) {
                String path = cacheEntity.getCachePath();
                if (path != null && path.length() > 0) {
                    FileUtils.deleteFiles(path);
                }
            }
        }
    }
}
