package org.zjuvipa.mapper;

import org.apache.ibatis.annotations.Param;
import org.zjuvipa.entity.Algorithm;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.zjuvipa.entity.User;
import org.zjuvipa.info.AlgorithmInfo;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author panyan
 * @since 2022-08-05
 */
public interface AlgorithmMapper extends BaseMapper<Algorithm> {

    public Algorithm findAlgoByName(String algorithmName);

    public List<Algorithm> searchAlgorithm();

}
