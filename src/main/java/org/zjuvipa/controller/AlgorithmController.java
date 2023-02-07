package org.zjuvipa.controller;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import org.zjuvipa.info.ModelInfo;
import org.zjuvipa.info.PictureDataInfo;
import org.zjuvipa.req.FindAlgoByNameReq;
import org.zjuvipa.req.GetResultsReq;
import org.zjuvipa.res.*;
import org.zjuvipa.service.IAlgorithmService;
import org.zjuvipa.service.IModelService;
import org.zjuvipa.service.IPictureDataService;
import org.zjuvipa.util.ResultBean;

import org.springframework.web.bind.annotation.*;

import org.zjuvipa.info.AlgorithmInfo;
import org.zjuvipa.info.DatasetInfo;
import org.zjuvipa.service.IDatasetService;
import org.zjuvipa.req.CallAlgorithmReq;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author panyan
 * @since 2022-08-05
 */


@Api(tags = "算法")
@RestController
@RequestMapping("/algorithm")
public class AlgorithmController {
    @Autowired
    IAlgorithmService iAlgorithmService;

    @Autowired
    IDatasetService iDatasetService;
    @Autowired
    IPictureDataService iPictureDataService;

    @Autowired
    IModelService iModelService;

    @CrossOrigin
    @ApiOperation("查询指定算法")
    @PostMapping("findAlgoByName")
    public ResultBean<FindAlgoByNameRes> findAlgo(@RequestBody FindAlgoByNameReq findAlgoByNameReq) {
        ResultBean<FindAlgoByNameRes> result = new ResultBean<FindAlgoByNameRes>();
        if(!StringUtils.hasText(findAlgoByNameReq.getAlgorithmName())){
            result.setCode(ResultBean.FAIL);
            result.setMsg("输入算法名称不合法！");
            result.setData(null);
            return result;
        }
        FindAlgoByNameRes  findAlgoByNameRes = new FindAlgoByNameRes();
        findAlgoByNameRes.setAlgorithmInfo(iAlgorithmService.findAlgoByName(findAlgoByNameReq.getAlgorithmName()));
        if(findAlgoByNameRes.getAlgorithmInfo() == null) {
            result.setCode(ResultBean.FAIL);
            result.setMsg("不存在该算法");
            result.setData(null);
        }
        else{
            result.setMsg("查找成功，算法名称为："+findAlgoByNameRes.getAlgorithmInfo().getAlgoName());
            result.setData(findAlgoByNameRes);
        }
        return result;
    }

    @CrossOrigin
    @ApiOperation("查询可用算法")
    @PostMapping("searchAlgorithm")
    public ResultBean<SearchAlgorithmRes> searchAlgorithm(){
        SearchAlgorithmRes searchAlgorithmRes = new SearchAlgorithmRes();
        searchAlgorithmRes.setAlgorithmInfos(iAlgorithmService.searchAlgorithm());
        ResultBean<SearchAlgorithmRes> result = new ResultBean<SearchAlgorithmRes>();
        result.setData(searchAlgorithmRes);
        return result;
    }

    @CrossOrigin
    @ApiOperation("调用算法")
    @PostMapping("callAlgorithm")
    public ResultBean<CallAlgorithmRes> callAlgorithm(@RequestBody CallAlgorithmReq callAlgorithmReq) throws IOException, InterruptedException {
        ResultBean<CallAlgorithmRes> result = new ResultBean<CallAlgorithmRes>();
        if(!StringUtils.hasText(callAlgorithmReq.getAlgorithmName())){
            result.setCode(ResultBean.FAIL);
            result.setMsg("输入算法名称不合法！");
            result.setData(null);
            return result;
        }
//        if(!StringUtils.hasText(callAlgorithmReq.getDatasetName())){
//            result.setCode(ResultBean.FAIL);
//            result.setMsg("输入数据集名称不合法！");
//            result.setData(null);
//            return result;
//        }
        if(!StringUtils.hasText(callAlgorithmReq.getUserName())){
            result.setCode(ResultBean.FAIL);
            result.setMsg("输入用户名称不合法！");
            result.setData(null);
            return result;
        }
        AlgorithmInfo algorithmInfo = iAlgorithmService.findAlgoByName(callAlgorithmReq.getAlgorithmName());
//        DatasetInfo datasetInfo = iDatasetService.findDatasetByUserAndName(callAlgorithmReq.getUserName(),callAlgorithmReq.getDatasetName());
        DatasetInfo datasetInfo = iDatasetService.getDatasetInfo(callAlgorithmReq.getDatasetId());
        if(algorithmInfo == null) {
            result.setCode(ResultBean.FAIL);
            result.setMsg("不存在该算法");
            result.setData(null);
            return result;
        }
        if(datasetInfo == null) {
            result.setCode(ResultBean.FAIL);
            result.setMsg("不存在该数据集");
            result.setData(null);
            return result;
        }
        if(datasetInfo.getDatasetIspublic() != 1){
            result.setCode(ResultBean.NO_PERMISSION);
            result.setMsg("非公开数据集");
            result.setData(null);
            return result;
        }

        List<PictureDataInfo> pictures = iPictureDataService.getPictures(datasetInfo.getDatasetId());
        int size1 = pictures.size();
//        String command1 = "python3 D:/Dam/algo.py";
        String command1 = "python3 /home/py/algo.py";
        String args = " --uName " + callAlgorithmReq.getUserName() + " --dName " + datasetInfo.getDatasetName() +
                " --aName " + callAlgorithmReq.getAlgorithmName() + " --size " + size1 + " --mName " +
                callAlgorithmReq.getModelName();

        String command2 = command1 + args;
        System.out.println(command2);
        Process process = Runtime.getRuntime().exec(command2);

        BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
        List<String> lines = new ArrayList<String>();
        String line;
        while ((line = in.readLine()) != null) {
            lines.add(line);
        }
        process.waitFor();
        CallAlgorithmRes callAlgorithmRes = new CallAlgorithmRes();
        callAlgorithmRes.setResult(lines);

        result.setData(callAlgorithmRes);
        result.setMsg(command2);
        return result;
    }

    private static List<GetResultsRes> readerMethod(File file) throws IOException {
        FileReader fileReader = new FileReader(file);
        Reader reader = new InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8);
        int ch = 0;
        StringBuilder sb = new StringBuilder();
        while ((ch = reader.read()) != -1) {
            sb.append((char) ch);
        }
        fileReader.close();
        reader.close();
        String jsonStr = sb.toString();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        return objectMapper.readValue(jsonStr, new TypeReference<List<GetResultsRes>>() {});
    }

    @CrossOrigin
    @ApiOperation("获取检测结果")
    @PostMapping("getResults")
    public ResultBean<List<GetResultsRes>> getResults(@RequestBody GetResultsReq getResultsReq) throws IOException {
        ResultBean<List<GetResultsRes>> result = new ResultBean<List<GetResultsRes>>();
        if(!StringUtils.hasText(getResultsReq.getModelName())){
            result.setCode(ResultBean.FAIL);
            result.setMsg("模型名称不合法！");
            result.setData(null);
            return result;
        }
//        String path = "D:/Dam/models/" + getResultsReq.getModelName() + "/result.json";
        String path = "/home/py/Dam/models/" + getResultsReq.getModelName() + "/result.json";
        File file = new File(path);
        List<GetResultsRes> lst = readerMethod(file);
        result.setData(lst);
        result.setMsg("success");
        return result;
    }

    @CrossOrigin
    @ApiOperation("获取日志")
    @PostMapping("getLog")
    public ResultBean<GetLogRes> getLog() throws IOException {
        String fileName = "D:\\Dam\\log.txt";
        List<String> lines = Files.readAllLines(Paths.get(fileName),
                StandardCharsets.UTF_8);
        GetLogRes getLogRes = new GetLogRes();
        getLogRes.setResult(lines);
        ResultBean<GetLogRes> result = new ResultBean<GetLogRes>();
        result.setData(getLogRes);
        return result;
    }
}
