package com.ehsunbehravesh.greenway;

import com.ehsunbehravesh.greenway.config.DatabaseConfig;
import com.ehsunbehravesh.greenway.constant.Constants;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.jdbc.JDBCClient;

/**
 *
 * @author ehsun7b
 */
public class AccessLogVerticle extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(AccessLogVerticle.class);

    @Override
    public void start(Future<Void> future) throws Exception {
        vertx.eventBus().consumer(Constants.ADDR_LOG_ACCESS, this::handleMessage);
    }

    private void handleMessage(Message<Object> message) {
        log.info("Log access request received.");

        try {
            String json = message.body().toString();
            log.debug("Log access request update json: " + json);

            JDBCClient client = JDBCClient.createShared(vertx, DatabaseConfig.INS.databaseConfig());

            client.getConnection(hndlr -> {
                if (hndlr.succeeded()) {
                    String sql = "INSERT INTO `tbl_access` (json) values(?)";
                    JsonArray params = new JsonArray();
                    params.add(json);
                    hndlr.result().updateWithParams(sql, params, result -> {
                        if (result.succeeded()) {
                            log.info("access logged.");
                        } else {
                            log.info("access logging failed.", result.cause());
                        }
                    });
                } else {
                    log.error(hndlr.cause().getMessage());
                }
            });

        } catch (NullPointerException ex) {
            log.error(ex.getMessage(), ex);
        }
    }

}
