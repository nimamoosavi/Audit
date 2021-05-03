package com.webold.audit.service;

import com.webold.core.mapper.jackson.Mapper;
import com.webold.core.packages.audit.view.AuditFactory;
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
    private static final Logger APP_LOG = LoggerFactory.getLogger("APP_LOG");


    @Override
    public void info(AuditFactory.AuditVM vm) {
        logInfile(vm, vm.getHeader().getLevel());
    }

    @Override
    public void error(AuditFactory.AuditVM vm) {
        logInfile(vm, vm.getHeader().getLevel());
    }

    @Override
    public void fatal(AuditFactory.AuditVM vm) {
        logInfile(vm, vm.getHeader().getLevel());
    }

    @Override
    public void warn(AuditFactory.AuditVM vm) {
        logInfile(vm, vm.getHeader().getLevel());
    }

    private void logInKafka(String kafkaTopic) {

    }

    private void logInfile(@NotNull Object o, @NotNull LogLevel level) {
        if (Boolean.TRUE.equals(level.name().equals(LogLevel.INFO.name())))
            APP_LOG.info(APP_LOG.getName(), o);
        if (Boolean.TRUE.equals(level.name().equals(LogLevel.ERROR.name())))
            APP_LOG.error(APP_LOG.getName(), o);
        if (Boolean.TRUE.equals(level.name().equals(LogLevel.FATAL.name())))
            APP_LOG.debug(APP_LOG.getName(), o);
        if (Boolean.TRUE.equals(level.name().equals(LogLevel.WARN.name())))
            APP_LOG.warn(APP_LOG.getName(), o);
    }

}
