package com.ehsunbehravesh.youtube;

import com.ehsunbehravesh.greenway.YouTubeGetInfoVerticle;
import com.ehsunbehravesh.greenway.constant.Constants;
import com.ehsunbehravesh.youtube.model.VideoProfile;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * @author ehsun7b
 */
public class YouTubeDl {

    private static final Logger log = LoggerFactory.getLogger(YouTubeDl.class);
    private static final String VIDEO_DIR;
    private static File DIR;
    
    static {
        VIDEO_DIR = System.getenv(Constants.VAR_VIDEO_DIR);
    }
    
    public VideoProfile getProfile(String url) throws IOException, InterruptedException, YouTubeDlException {
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
                    result.setDuration(line);
                    break;
                case 6:
                    result.setFormat(line);
                    break;
            }

            //System.out.println(line);
            log.debug("youtube-dl output line " + i + ": " + line);
        }

        int exitVal = proc.waitFor();

        if (exitVal != 0) {
            throw new YouTubeDlException("youtube-dl exit code: " + exitVal);
        }

        log.info("youtube-dl exit code: " + exitVal);

        return result;
    }

    public void download(VideoProfile videoProfile) throws IOException, InterruptedException, YouTubeDlException {
        if (DIR == null) {
            DIR = new File(VIDEO_DIR.concat(videoProfile.getId()));
        }
        
        if (videoExists(videoProfile)) {
            log.info("The video is already existing. ID: " + videoProfile.getId());
        } else {
            log.info("The video does NOT exist. ID: " + videoProfile.getId());
            log.info("Downloading the video. ID: " + videoProfile.getId());

            Process proc = Runtime.getRuntime().exec("cd " + DIR.getAbsolutePath() + "\nyoutube-dl  "
                    + " ".concat(videoProfile.getUrl()));

            InputStream stdin = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(stdin);
            BufferedReader br = new BufferedReader(isr);

            String line;
            
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                log.debug("youtube-dl output line: " + line);
            }

            int exitVal = proc.waitFor();

            if (exitVal != 0) {
                throw new YouTubeDlException("youtube-dl exit code: " + exitVal);
            }

            log.info("youtube-dl exit code: " + exitVal);
        }
    }

    private boolean videoExists(VideoProfile videoProfile) {
        if (DIR == null) {
            DIR = new File(VIDEO_DIR.concat(videoProfile.getId()));
        }

        if (DIR.exists() && DIR.isDirectory()) {
            File video = new File(DIR, videoProfile.getFilename());

            return video.exists() && video.isFile();
        }

        return false;
    }
}
