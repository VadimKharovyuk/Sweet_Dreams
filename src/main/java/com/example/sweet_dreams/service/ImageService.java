package com.example.sweet_dreams.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;


@Service
@RequiredArgsConstructor
public class ImageService {

    private static final long MAX_FILE_SIZE = 1024 * 1024; // 1MB
    private static final String[] ALLOWED_CONTENT_TYPES = {
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/webp"  // Добавляем WebP
    };

    public byte[] convertToBytes(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Файл не может быть пустым");
        }

        // Проверка размера файла
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Размер файла превышает 1MB");
        }

        // Проверка типа файла
        String contentType = file.getContentType();
        if (contentType == null || !isAllowedContentType(contentType)) {
            throw new IllegalArgumentException("Неподдерживаемый тип файла. Разрешены только JPEG, PNG, GIF и WebP");
        }

        try {
            // Если это не WebP, можно конвертировать в WebP для оптимизации
            if (!"image/webp".equals(contentType)) {
                return convertToWebP(file.getBytes());
            }
            return file.getBytes();
        } catch (IOException e) {
            throw new IOException("Ошибка при чтении файла", e);
        }
    }

    private byte[] convertToWebP(byte[] imageData) {
        try {
            // Создаем буфер изображения из исходных данных
            BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(imageData));

            // Создаем выходной поток для WebP
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            // Конвертируем в WebP
            ImageIO.write(originalImage, "webp", outputStream);

            return outputStream.toByteArray();
        } catch (IOException e) {
            // Если конвертация не удалась, возвращаем оригинальные данные
            return imageData;
        }
    }

    public String convertToBase64(byte[] image) {
        if (image == null || image.length == 0) {
            return null;
        }
        try {
            return Base64.getEncoder().encodeToString(image);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при конвертации изображения в Base64", e);
        }
    }

    private boolean isAllowedContentType(String contentType) {
        return Arrays.asList(ALLOWED_CONTENT_TYPES).contains(contentType);
    }

    // Оптимизация изображения
    public byte[] optimizeImage(byte[] imageData, String contentType) throws IOException {
        try {
            BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(imageData));

            // Определяем новые размеры (например, максимальная ширина 800px)
            int maxWidth = 800;
            int newWidth = Math.min(originalImage.getWidth(), maxWidth);
            int newHeight = (int) ((float) originalImage.getHeight() * newWidth / originalImage.getWidth());

            // Создаем новое изображение с новыми размерами
            BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = resizedImage.createGraphics();
            g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
            g.dispose();

            // Конвертируем в WebP с заданным качеством
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, "webp", outputStream);

            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new IOException("Ошибка при оптимизации изображения", e);
        }
    }

    // Метод для получения размера изображения
    public Dimension getImageDimensions(byte[] imageData) throws IOException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
        return new Dimension(image.getWidth(), image.getHeight());
    }

    // Проверка размеров изображения
    public boolean isValidImageDimensions(byte[] imageData, int maxWidth, int maxHeight) throws IOException {
        Dimension dimensions = getImageDimensions(imageData);
        return dimensions.width <= maxWidth && dimensions.height <= maxHeight;
    }
}