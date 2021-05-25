package com.nicico.cost.audit.service;

import com.nicico.cost.audit.enums.Locations;
import com.nicico.cost.framework.packages.audit.view.AuditFactory;
import com.nicico.cost.framework.packages.kafka.KafkaProducer;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

@Component
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {


    @Value("${audit.log.location:file}")
    public String logLocation;
    @Value("${audit.log.topic:audit}")
    public String kafkaTopic;
    private static final Logger APP_LOG = LoggerFactory.getLogger("APP_LOG");
    @Autowired(required = false)
    public KafkaProducer kafkaProducer;


    @Override
    public void info(AuditFactory.AuditVM vm) {
        log(vm);
    }

    @Override
    public void error(AuditFactory.AuditVM vm) {
        log(vm);
    }

    @Override
    public void fatal(AuditFactory.AuditVM vm) {
        log(vm);
    }

    @Override
    public void warn(AuditFactory.AuditVM vm) {
        log(vm);
    }

    private void logInKafka(String kafkaTopic, Object o) {
        kafkaProducer.send(kafkaTopic, o);
    }

    private void log(AuditFactory.AuditVM vm) {
        if (Boolean.TRUE.equals(logLocation.equals(Locations.FILE.name())))
            logInfile(vm, vm.getHeader().getLevel());
        if (Boolean.TRUE.equals(logLocation.equals(Locations.KAFKA.name())))
            logInKafka(kafkaTopic, vm);
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
