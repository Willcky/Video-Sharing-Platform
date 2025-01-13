package org.dromara.common.ffmpeg.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class VideoTransResult {
    /**
     * outputDir path
     */
    private String outputDir;

    /**
     * resolutions
     */
    private List<String> resolutions;
    /**
     * duration
     */
    private long duration;
}
