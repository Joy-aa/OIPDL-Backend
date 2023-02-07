package org.zjuvipa.service.impl;

import org.zjuvipa.entity.Algorithm;
import org.zjuvipa.info.AlgorithmInfo;
import org.zjuvipa.mapper.AlgorithmMapper;
import org.zjuvipa.service.IAlgorithmService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author panyan
 * @since 2022-08-05
 */
@Service
public class AlgorithmServiceImpl extends ServiceImpl<AlgorithmMapper, Algorithm> implements IAlgorithmService {
    @Resource
    AlgorithmMapper algorithmMapper;

    @Override
    public AlgorithmInfo findAlgoByName(String algorithmName){
        Algorithm algorithm = algorithmMapper.findAlgoByName(algorithmName);
        if(algorithm == null)
            return null;
        else
            return algorithm.change();
    }

    @Override
    public List<AlgorithmInfo> searchAlgorithm(){
        List<Algorithm> temp= algorithmMapper.searchAlgorithm();
        List<AlgorithmInfo> result = new ArrayList<AlgorithmInfo>();
        for (Algorithm obj: temp){
            result.add(obj.change());
        }
        return result;
    }
}
