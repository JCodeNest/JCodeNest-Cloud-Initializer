package cn.jcodenest.framework.operatelog.core.service;

import cn.jcodenest.framework.common.biz.system.logger.OperateLogCommonApi;
import cn.jcodenest.framework.common.biz.system.logger.dto.OperateLogCreateReqDTO;
import cn.jcodenest.framework.common.util.monitor.TracerUtils;
import cn.jcodenest.framework.common.util.servlet.ServletUtils;
import cn.jcodenest.framework.security.core.LoginUser;
import cn.jcodenest.framework.security.core.util.SecurityFrameworkUtils;
import com.mzt.logapi.beans.LogRecord;
import com.mzt.logapi.service.ILogRecordService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 操作日志 ILogRecordService 实现类
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/1
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Slf4j
public class LogRecordServiceImpl implements ILogRecordService {

    @Resource
    private OperateLogCommonApi operateLogCommonApi;

    /**
     * 记录操作日志
     *
     * @param logRecord 操作日志
     */
    @Override
    public void record(LogRecord logRecord) {
        OperateLogCreateReqDTO reqDTO = new OperateLogCreateReqDTO();
        try {
            // 补充链路追踪编号
            reqDTO.setTraceId(TracerUtils.getTraceId());
            // 补充用户信息
            fillUserFields(reqDTO);
            // 补全模块信息
            fillModuleFields(reqDTO, logRecord);
            // 补全请求信息
            fillRequestFields(reqDTO);

            // 异步记录日志
            operateLogCommonApi.createOperateLogAsync(reqDTO);
        } catch (Throwable ex) {
            // 由于 @Async 异步调用, 这里打印下日志更容易跟进
            log.error("[record][url({}) log({}) 发生异常]", reqDTO.getRequestUrl(), reqDTO, ex);
        }
    }

    /**
     * 查询操作日志
     *
     * @param bizNo 业务编号
     * @param type  模块类型
     * @return 操作日志
     */
    @Override
    public List<LogRecord> queryLog(String bizNo, String type) {
        throw new UnsupportedOperationException("使用 OperateLogApi 进行操作日志的查询");
    }

    /**
     * 查询操作日志
     *
     * @param bizNo   业务编号
     * @param type    模块类型
     * @param subType 操作名称
     * @return 操作日志
     */
    @Override
    public List<LogRecord> queryLogByBizNo(String bizNo, String type, String subType) {
        throw new UnsupportedOperationException("使用 OperateLogApi 进行操作日志的查询");
    }

    /**
     * 补充用户信息
     *
     * @param reqDTO 请求
     */
    private static void fillUserFields(OperateLogCreateReqDTO reqDTO) {
        // 使用 SecurityFrameworkUtils, 因为要考虑 rpc、mq、job 它其实不是 web
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser == null) {
            return;
        }

        reqDTO.setUserId(loginUser.getId());
        reqDTO.setUserType(loginUser.getUserType());
    }

    /**
     * 补充模块信息
     *
     * @param reqDTO    请求
     * @param logRecord 操作日志
     */
    public static void fillModuleFields(OperateLogCreateReqDTO reqDTO, LogRecord logRecord) {
        // 大模块类型, 例如：CRM 客户
        reqDTO.setType(logRecord.getType());
        // 操作名称, 例如：转移客户
        reqDTO.setSubType(logRecord.getSubType());
        // 业务编号, 例如：客户编号
        reqDTO.setBizId(Long.parseLong(logRecord.getBizNo()));
        // 操作内容, 例如：修改编号为 1 的用户信息, 将性别从男改成女, 将姓名从张三改为李四
        reqDTO.setAction(logRecord.getAction());
        // 拓展字段, 有些复杂的业务需要记录一些字段 (JSON 格式), 例如: 记录订单编号, { orderId: "1"}
        reqDTO.setExtra(logRecord.getExtra());
    }

    /**
     * 补充请求信息
     *
     * @param reqDTO 请求
     */
    private static void fillRequestFields(OperateLogCreateReqDTO reqDTO) {
        // 获取 Request 对象
        HttpServletRequest request = ServletUtils.getRequest();
        if (request == null) {
            return;
        }

        // 补全请求信息
        reqDTO.setRequestMethod(request.getMethod());
        reqDTO.setRequestUrl(request.getRequestURI());
        reqDTO.setUserIp(ServletUtils.getClientIP(request));
        reqDTO.setUserAgent(ServletUtils.getUserAgent(request));
    }
}
