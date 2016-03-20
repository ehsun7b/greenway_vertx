package com.ehsunbehravesh.youtube;

import com.ehsunbehravesh.greenway.YouTubeGetInfoVerticle;
import com.ehsunbehravesh.youtube.model.VideoProfile;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * @author ehsun7b
 */
public class YouTubeDl {

    private static final Logger log = LoggerFactory.getLogger(YouTubeDl.class);
    
    public VideoProfile getProfile(String url) throws IOException, InterruptedException {
        VideoProfile result = new VideoProfile(url);

        Process proc = Runtime.getRuntime().exec("youtube-dl --get-id --get-title --get-thumbnail "
                + "--get-duration --get-filename --get-format ".concat(url));

        InputStream stdin = proc.getInputStream();
        InputStreamReader isr = new InputStreamReader(stdin);
        BufferedReader br = new BufferedReader(isr);

        String line;
        int i = 0;
        while ((line = br.readLine()) != null) {
            i++;

            switch (i) {
                case 1:
                    result.setTitle(line);
                    break;
                case 2:
                    result.setId(line);
                    break;
                case 3:
                    result.setThumbnailUrl(line);
                    break;
                case 4:
                    result.setFilename(line);
                    break;
                case 5:
                    result.setThumbnailUrl(line);
                    break;
            }
            System.out.println(line);
            log.debug("youtube-dl output line " + i + ": " + line);
        }

        int exitVal = proc.waitFor();
        log.info("youtube-dl exit code: " + exitVal);

        return result;
    }
}
