package com.coderdream.service.impl;

import com.coderdream.entity.DictionaryEntry;
import com.coderdream.mapper.DictionaryEntryMapper;
import com.coderdream.service.DictionaryEntryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.cd.CdListUtil;
import jakarta.annotation.Resource;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
@Slf4j
public class DictionaryEntryServiceImpl extends ServiceImpl<DictionaryEntryMapper, DictionaryEntry> implements
    DictionaryEntryService {


    @Resource
    private DictionaryEntryMapper dictionaryEntryMapper;

    @Override
    public DictionaryEntry getDictionaryByWord(String word) {
        return baseMapper.selectByWord(word);
    }

    @Override
    @Transactional
    public int insertOrUpdateBatch(List<DictionaryEntry> dictionaryEntryList) {
        int count = 0;
        if (!CollectionUtils.isEmpty(dictionaryEntryList)) {
            log.info("本次批量执行的记录条数: {} ", dictionaryEntryList.size());
            // 分批处理
            List<List<DictionaryEntry>> lists = CdListUtil.splitTo(dictionaryEntryList, CdConstants.BATCH_INSERT_UPDATE_ROWS);
            for (List<DictionaryEntry> list : lists) {
                count += dictionaryEntryMapper.insertOrUpdateBatch(list);
            }
        }

        return count;
    }
}
