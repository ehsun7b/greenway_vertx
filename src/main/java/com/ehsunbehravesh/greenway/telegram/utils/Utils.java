package com.ehsunbehravesh.greenway.telegram.utils;

import com.ehsunbehravesh.greenway.GreenWayBot;
import com.ehsunbehravesh.greenway.constant.Constants;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author ehsun7b
 */
public class Utils {

    private static final Logger log = LoggerFactory.getLogger(Utils.class);

    private static final int MIN_LENGTH_OF_LONG_TEXT = 10;

    public static boolean isYouTubeLink(String text) {
        if (text != null && text.trim().length() > 10) {
            text = text.trim();

            String pattern = "https?:\\/\\/(?:[0-9A-Z-]+\\.)?(?:youtu\\.be\\/|youtube\\.com\\S*[^\\w\\-\\s])([\\w\\-]{11})(?=[^\\w\\-]|$)(?![?=&+%\\w]*(?:['\"][^<>]*>|<\\/a>))[?=&+%\\w]*";

            Pattern compiledPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
            Matcher matcher = compiledPattern.matcher(text);

            return matcher.matches();
        }

        return false;
    }

    public static boolean isDownloadCommand(String text) {
        if (text != null && text.trim().length() > 0) {
            if (text.equalsIgnoreCase(Constants.CMD_DOWNLOAD)
                    || text.equalsIgnoreCase(Constants.CMD_DOWNLOAD.concat(GreenWayBot.BOT_TAG))) {
                return true;
            }
        }

        return false;
    }

    public static boolean isLongText(String text) {
        if (text != null && text.trim().length() > MIN_LENGTH_OF_LONG_TEXT) {
            return true;
        }

        return false;
    }

    public static String shortenText(String body) {
        return truncate(body, 50);
    }

    public static String truncate(final String content, final int lastIndex) {
        String result = content.substring(0, lastIndex);
        if (content.charAt(lastIndex) != ' ') {
            result = result.substring(0, result.lastIndexOf(" "));
        }
        return result;
    }

}
