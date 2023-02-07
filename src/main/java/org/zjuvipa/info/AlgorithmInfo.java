package org.zjuvipa.info;

import org.zjuvipa.entity.Algorithm;
import org.zjuvipa.util.MyBeanUtils;

import lombok.Data;

@Data
public class AlgorithmInfo {

    private Integer algoId;

    private String algoName;

    public AlgorithmInfo(){}

    public AlgorithmInfo(Algorithm algorithm){
        MyBeanUtils.copyProperties(algorithm, this);
    }
}
