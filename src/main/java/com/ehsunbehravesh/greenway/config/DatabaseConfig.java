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
        config.put("url", "jdbc:mysql://localhost:3306/greenway?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
        config.put("user", "root");
        config.put("password", "13621215ShahiShima");
        config.put("driver_class", "com.mysql.jdbc.Driver");

        return config;
    }
}
