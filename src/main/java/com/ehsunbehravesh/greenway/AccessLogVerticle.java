package com.ehsunbehravesh.greenway;

import com.ehsunbehravesh.greenway.constant.Constants;
import com.ehsunbehravesh.greenway.telegram.model.Update;
import com.ehsunbehravesh.youtube.YouTubeDl;
import com.ehsunbehravesh.youtube.YouTubeDlException;
import com.ehsunbehravesh.youtube.model.VideoProfile;
import com.google.gson.Gson;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.jdbc.JDBCClient;
import java.io.IOException;

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

            JsonObject config = new JsonObject();
            config.put("url", "jdbc:mysql://localhost:3306/greenway");
            config.put("user", "root");
            config.put("password", "13621215ShahiShima");

            JDBCClient client = JDBCClient.createShared(vertx, config);

            client.getConnection(hndlr -> {
                if (hndlr.succeeded()) {
                    String sql = "INSERT INTO `tbl_access` (json) values(?)";
                    JsonArray params = new JsonArray();
                    params.add(json);
                    hndlr.result().updateWithParams(sql, params, result -> {
                        if (result.succeeded()) {
                            log.info("access log inserted.");
                        } else {
                            log.info("access log insert failed.", result.cause());
                        }
                    });
                } else {
                    System.out.println(hndlr.cause().getMessage());
                }
            });

        } catch (NullPointerException ex) {
            log.error(ex.getMessage(), ex);
        }
    }

}
