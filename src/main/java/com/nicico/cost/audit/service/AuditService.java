package com.nicico.cost.audit.service;

import com.nicico.cost.framework.packages.audit.Log;

/**
 * @version 1.0.1
 * @apiNote default log store is in file but you can change it by properties audit.log.location
 * and default log Topic is in audit but you can change it by set properties audit.log.topic
 */
public interface AuditService extends Log {

}
