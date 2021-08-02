package app.ladderproject.audit.service;


import app.ladderproject.core.packages.audit.Log;

/**
 * @version 1.0.1
 * @since 1.8
 * @apiNote default log store is in file But you can change it by properties audit.log.location
 * and default log Topic is in audit But you can change it by set properties audit.log.topic
 */
public interface AuditService extends Log {

}
