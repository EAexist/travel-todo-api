package com.matchalab.trip_todo_api.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;

import javax.imageio.ImageIO;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Utils {

    public static URI getLocation(Object resourceId) {
        return ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{childId}").buildAndExpand(resourceId)
                .toUri();
    }

    public static BufferedImage multipartFileToBufferedImage(MultipartFile file) {
        try (ByteArrayInputStream in = new ByteArrayInputStream(file.getBytes())) {
            return ImageIO.read(in);
        } catch (IOException e) {
            return null;
        }
    }

    public static Resource bufferedImageToTiffResource(BufferedImage bufferedImage) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            File file = new File("file.tiff");
            ImageIO.write(bufferedImage, "TIFF", file);
            return new ClassPathResource("file.tiff");
            // ImageIO.write(bufferedImage, "tiff", os);
            // byte[] tiffBytes = os.toByteArray();
            // return new ByteArrayResource(tiffBytes);
        } catch (IOException e) {
            return null;
        }
    }

    public static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            final String jsonContent = mapper.writeValueAsString(obj);
            return jsonContent;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String timeStringToIsoDateTimeString(String timeString, String dateOnlyIsoString) {
        return (timeString != null
                && timeString.length() == 5
                && dateOnlyIsoString != null)
                        ? String.format("%sT%s", dateOnlyIsoString,
                                timeString)
                        : timeString;
    }
}
