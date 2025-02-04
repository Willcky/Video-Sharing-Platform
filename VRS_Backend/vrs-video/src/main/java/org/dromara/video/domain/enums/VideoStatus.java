package org.dromara.video.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Video Status Enum
 */
@Getter
@AllArgsConstructor
public enum VideoStatus {

    PUBLISHED(0, "已发布"),
    REVIEWING(1, "审核中"),
    OFFLINE(2, "已下架"),
    TRANSCODING(3, "转码中"),
    TRANSCODE_FAILED(4, "转码失败"),
    UPLOADED(5, "已上传"),
    PENDING_TRANSCODE(6, "待转码"),
    TRANSCODED(7, "转码完成"),
    PENDING_UPLOAD(8, "待上传"),
    UPLOADING(9, "上传中"),
    UPLOAD_FAILED(10, "上传失败");

    private final Integer code;
    private final String info;

    /**
     * Get enum by code
     */
    public static VideoStatus getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (VideoStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
