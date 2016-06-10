package com.ehsunbehravesh.greenway.config;

import io.vertx.core.json.JsonObject;

/**
 *
 * @author ehsun7b
 */
public enum DatabaseConfig {
    INS;

    public JsonObject databaseConfig() {
        JsonObject config = new JsonObject();
        config.put("url", "jdbc:mysql://localhost:3306/greenway?useUnicode=true&characterEncoding=UTF-8&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=MYT&autoReconnect=true&useSSL=false");
        config.put("user", "root");
        config.put("password", "13621215ShahiShima");
        config.put("driver_class", "com.mysql.jdbc.Driver");

        return config;
    }
}
