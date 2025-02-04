package org.dromara.interaction.utils;

import org.dromara.common.core.exception.ServiceException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 图片验证工具类
 */
public class ImageValidationUtil {

    /**
     * 允许的图片格式
     */
    private static final Set<String> ALLOWED_IMAGE_TYPES = new HashSet<>(Arrays.asList(
        "image/jpeg",
        "image/png",
        "image/webp"
    ));

    /**
     * 最大文件大小 (5MB)
     */
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    /**
     * 验证图片格式和大小
     *
     * @param image 图片文件
     * @throws ServiceException 当图片验证失败时抛出异常
     */
    public static void validateImage(MultipartFile image) {
        // 验证文件大小
        if (image.getSize() > MAX_FILE_SIZE) {
            throw new ServiceException("图片大小不能超过5MB");
        }

        // 验证文件类型
        String contentType = image.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new ServiceException("不支持的图片格式，仅支持JPG、PNG和WEBP格式");
        }

        // 进一步验证文件头
        try {
            byte[] bytes = image.getBytes();
            if (bytes.length < 8) {
                throw new ServiceException("无效的图片文件");
            }

            // 验证文件魔数
            boolean isValidImage = false;
            byte[] header = Arrays.copyOfRange(bytes, 0, 8);

            // JPEG (FF D8 FF)
            if (bytes[0] == (byte) 0xFF && bytes[1] == (byte) 0xD8 && bytes[2] == (byte) 0xFF) {
                isValidImage = true;
            }
            // PNG (89 50 4E 47 0D 0A 1A 0A)
            else if (header[0] == (byte) 0x89 && header[1] == (byte) 0x50 && header[2] == (byte) 0x4E &&
                     header[3] == (byte) 0x47 && header[4] == (byte) 0x0D && header[5] == (byte) 0x0A &&
                     header[6] == (byte) 0x1A && header[7] == (byte) 0x0A) {
                isValidImage = true;
            }
            // WEBP (52 49 46 46 ... 57 45 42 50)
            else if (bytes[0] == (byte) 0x52 && bytes[1] == (byte) 0x49 && bytes[2] == (byte) 0x46 &&
                     bytes[3] == (byte) 0x46 && bytes.length > 12 && bytes[8] == (byte) 0x57 &&
                     bytes[9] == (byte) 0x45 && bytes[10] == (byte) 0x42 && bytes[11] == (byte) 0x50) {
                isValidImage = true;
            }

            if (!isValidImage) {
                throw new ServiceException("无效的图片文件格式");
            }
        } catch (IOException e) {
            throw new ServiceException("图片验证失败");
        }
    }

    private ImageValidationUtil() {
        // 私有构造函数，防止实例化
    }
}
