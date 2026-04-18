package com.swp391.OnlineLearning.util;

import com.swp391.OnlineEnglishLearningSystem.model.Question;
import com.swp391.OnlineEnglishLearningSystem.model.dto.CourseDTO;
import com.swp391.OnlineEnglishLearningSystem.model.dto.MultipleChoiceQuestionFormDTO;
import com.swp391.OnlineEnglishLearningSystem.model.dto.ShortAnswerQuestionFormDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

// Logic cho annotation @ValidMediaType, áp dụng cho class DTO
public class ValidMediaTypeValidator implements ConstraintValidator<ValidMediaType, Object> { // Dùng Object để có thể tái sử dụng cho các DTO khác

    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "bmp", "webp");
    private static final Set<String> ALLOWED_VIDEO_EXTENSIONS = Set.of("mp4", "avi", "mkv", "mov", "webm");
    private static final Set<String> ALLOWED_AUDIO_EXTENSIONS = Set.of("mp3", "aac", "wav", "ogg");

    @Override
    public boolean isValid(Object dto, ConstraintValidatorContext context) {

        try {
            if (dto == null) return true;

            Question.MediaType mediaType = null;
            MultipartFile mediaFile = null;

            if (dto instanceof MultipleChoiceQuestionFormDTO questionDTO) {
                mediaType = questionDTO.getMediaType();
                mediaFile = questionDTO.getMedia();

            } else if (dto instanceof ShortAnswerQuestionFormDTO questionDTO) {
                mediaType = questionDTO.getMediaType();
                mediaFile = questionDTO.getMedia();

            } else if (dto instanceof CourseDTO courseDTO) {
                mediaType = Question.MediaType.IMAGE;
                mediaFile = courseDTO.getThumbnailFile();

            } else {
                return true; // ❗ cực kỳ quan trọng
            }

            if (mediaType == null) return true;

            if (mediaFile == null || mediaFile.isEmpty()) return true;

            if (mediaType == Question.MediaType.NONE) return false;

            String originalFilename = mediaFile.getOriginalFilename();
            if (originalFilename == null || !originalFilename.contains(".")) {
                return false;
            }

            String extension = originalFilename
                    .substring(originalFilename.lastIndexOf(".") + 1)
                    .toLowerCase();

            return switch (mediaType) {
                case IMAGE -> ALLOWED_IMAGE_EXTENSIONS.contains(extension);
                case VIDEO -> ALLOWED_VIDEO_EXTENSIONS.contains(extension);
                case AUDIO -> ALLOWED_AUDIO_EXTENSIONS.contains(extension);
                default -> false;
            };

        } catch (Exception e) {
            e.printStackTrace(); // 👈 in lỗi ra log
            return false; // ❗ tránh crash HV000028
        }
    }
}