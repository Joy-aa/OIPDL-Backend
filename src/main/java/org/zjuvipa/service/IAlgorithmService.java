package org.zjuvipa.service;

import org.zjuvipa.entity.Algorithm;
import com.baomidou.mybatisplus.extension.service.IService;
import org.zjuvipa.info.AlgorithmInfo;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author panyan
 * @since 2022-08-05
 */
public interface IAlgorithmService extends IService<Algorithm> {
    public List<AlgorithmInfo> searchAlgorithm();
    public AlgorithmInfo findAlgoByName(String algorithmName);
}
