package com.webold.audit.service;

import com.webold.core.mapper.jacson.Mapper;
import com.webold.core.packages.audit.Audit;
import com.webold.core.packages.audit.view.AuditVM;
import com.webold.core.packages.audit.view.file.FileReq;
import com.webold.core.utility.ApplicationRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements Audit {


    private final Mapper mapper;
    private final ApplicationRequest request;
    private static final Logger APP_LOG = LoggerFactory.getLogger("APP_LOG");

    @Override
    public void log(AuditVM vm) {
        if (vm.getAuditLocation().getFile() != null) {
            if (Boolean.TRUE.equals(vm.getAuditLocation().getFile().getType().name().equals(FileReq.Type.LOG4J.name())))
                logInfile(vm, vm.getHeader().getLevel());
            else if (Boolean.TRUE.equals(vm.getAuditLocation().getFile().getType().name().equals(FileReq.Type.JSON.name())))
                logInfile(mapper.convertToJson(vm), vm.getHeader().getLevel());
        }
        if (vm.getAuditLocation().getKafka() != null) {

        }

    }


    private void logInfile(@NotNull Object o, @NotNull LogLevel level) {
        if (Boolean.TRUE.equals(level.name().equals(LogLevel.INFO.name())))
            APP_LOG.info(APP_LOG.getName(), o);
        if (Boolean.TRUE.equals(level.name().equals(LogLevel.ERROR.name())))
            APP_LOG.error(APP_LOG.getName(), o);
        if (Boolean.TRUE.equals(level.name().equals(LogLevel.DEBUG.name())))
            APP_LOG.debug(APP_LOG.getName(), o);
        if (Boolean.TRUE.equals(level.name().equals(LogLevel.WARN.name())))
            APP_LOG.warn(APP_LOG.getName(), o);
        if (Boolean.TRUE.equals(level.name().equals(LogLevel.TRACE.name())))
            APP_LOG.trace(APP_LOG.getName(), o);
    }

}
