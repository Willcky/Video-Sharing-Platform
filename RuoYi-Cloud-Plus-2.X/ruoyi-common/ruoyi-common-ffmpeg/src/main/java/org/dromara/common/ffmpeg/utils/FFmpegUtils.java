package org.dromara.common.ffmpeg.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.ffmpeg.config.FFmpegConfig;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * FFmpeg Utility for Video Processing
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FFmpegUtils {

    private final FFmpegConfig config;

    private FFmpeg ffmpeg;
    private FFprobe ffprobe;

    /**
     * Initialize FFmpeg and FFprobe
     */
    public void init() {
        try {
            ffmpeg = new FFmpeg(config.getFfmpegPath());
            ffprobe = new FFprobe(config.getFfprobePath());
        } catch (IOException e) {
            log.error("Failed to initialize FFmpeg: ", e);
            throw new ServiceException("FFmpeg initialization failed");
        }
    }



    /**
     * Convert video to HLS format with multiple quality levels
     *
     * @param inputPath Input video file path
     * @param videoFileId video file id
     * @return List of generated m3u8 playlist paths
     */
    public List<String> convertToHls(String inputPath, Long videoFileId) {
        try {
            if (ffmpeg == null || ffprobe == null) {
                init();
            }
            String outputDir = config.getOutputPath() + File.separator + videoFileId;
            // Create output directory if it doesn't exist
            Files.createDirectories(Paths.get(outputDir));

            // Get video information
            FFmpegProbeResult probeResult = ffprobe.probe(inputPath);
            int originalHeight = probeResult.getStreams().get(0).height;

            List<String> playlistPaths = new ArrayList<>();

            // Create variants for different qualities
            for (int i = 0; i < config.getVideoResolutions().length; i++) {
                int targetHeight = config.getVideoResolutions()[i];

                // Skip if target resolution is higher than original
                if (targetHeight > originalHeight) {
                    continue;
                }

                String variantDir = outputDir + File.separator + targetHeight + "p";
                Files.createDirectories(Paths.get(variantDir));

                String variantM3u8 = variantDir + File.separator + "index.m3u8";
                playlistPaths.add(variantM3u8);

                FFmpegBuilder builder = new FFmpegBuilder()
                    .setInput(inputPath)
                    .overrideOutputFiles(true)
                    .addOutput(variantM3u8)
                    .setFormat("hls")
                    .setVideoCodec("libx264")
                    .setAudioCodec("aac")
                    .setVideoResolution(calculateWidth(targetHeight, probeResult), targetHeight)
                    .setVideoBitRate(config.getVideoBitrates()[i] * 1000L) // Convert kbps to bps
                    .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL)
                    .addExtraArgs("-hls_time", String.valueOf(config.getHlsTime()))
                    .addExtraArgs("-hls_list_size", String.valueOf(config.getHlsListSize()))
                    .addExtraArgs("-hls_segment_filename", variantDir + File.separator + "segment_%03d.ts")
                    .done();

                FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
                executor.createJob(builder).run();
            }

            // Create master playlist
            createMasterPlaylist(outputDir, playlistPaths, config.getVideoResolutions());

            return playlistPaths;

        } catch (IOException e) {
            log.error("Failed to convert video to HLS: ", e);
            throw new ServiceException("Video transcoding failed");
        }
    }

    /**
     * Create master playlist for HLS adaptive streaming
     */
    private void createMasterPlaylist(String outputDir, List<String> variantPlaylists, int[] resolutions) throws IOException {
        Path masterPlaylistPath = Paths.get(outputDir, "master.m3u8");
        List<String> lines = new ArrayList<>();
        lines.add("#EXTM3U");
        lines.add("#EXT-X-VERSION:3");

        for (int i = 0; i < variantPlaylists.size(); i++) {
            String variantPath = variantPlaylists.get(i);
            int resolution = resolutions[i];
            int bandwidth = config.getVideoBitrates()[i] * 1000; // Convert to bps

            lines.add("#EXT-X-STREAM-INF:BANDWIDTH=" + bandwidth + ",RESOLUTION=" + calculateWidth(resolution, null) + "x" + resolution);
            lines.add(resolution + "p/index.m3u8");
        }

        Files.write(masterPlaylistPath, lines);
    }

    /**
     * Calculate video width based on height while maintaining aspect ratio
     */
    private int calculateWidth(int targetHeight, FFmpegProbeResult probeResult) {
        if (probeResult != null) {
            double aspectRatio = (double) probeResult.getStreams().get(0).width / probeResult.getStreams().get(0).height;
            return (int) (targetHeight * aspectRatio);
        }
        return targetHeight * 16 / 9; // Default to 16:9 aspect ratio
    }
}
