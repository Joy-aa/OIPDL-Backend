package org.zjuvipa.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.zjuvipa.req.GetUserHistoriesReq;
import org.zjuvipa.res.GetUserHistoriesRes;
import org.zjuvipa.service.IHistoriesService;
import org.zjuvipa.util.ResultBean;

import javax.annotation.Resource;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author panyan
 * @since 2022-09-22
 */
@RestController
@RequestMapping("/histories")
public class HistoriesController {

    @Autowired
    IHistoriesService iHistoriesService;

    @CrossOrigin
    @PostMapping("/getUserHistories")
    public ResultBean<GetUserHistoriesRes> getUserHistories(@RequestBody GetUserHistoriesReq req) {
        ResultBean<GetUserHistoriesRes> result = new ResultBean<>();
        GetUserHistoriesRes res = new GetUserHistoriesRes();
        res.setHistoriesInfos(iHistoriesService.getUserHistories(req.getUsername()));
        return result;
    }


}
