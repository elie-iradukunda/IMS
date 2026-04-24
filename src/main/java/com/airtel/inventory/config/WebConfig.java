package com.airtel.inventory.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new LenientEnumConverterFactory());
    }

    private static final class LenientEnumConverterFactory implements ConverterFactory<String, Enum> {

        @Override
        @SuppressWarnings({ "rawtypes", "unchecked" })
        public <T extends Enum> Converter<String, T> getConverter(Class<T> targetType) {
            return new LenientEnumConverter(targetType);
        }
    }

    private static final class LenientEnumConverter<T extends Enum<T>> implements Converter<String, T> {

        private final Class<T> targetType;

        private LenientEnumConverter(Class<T> targetType) {
            this.targetType = targetType;
        }

        @Override
        public T convert(String source) {
            if (source == null || source.isBlank()) {
                return null;
            }

            String raw = source.trim();
            String normalizedInput = normalize(raw);

            for (T constant : targetType.getEnumConstants()) {
                if (constant.name().equalsIgnoreCase(raw)
                        || constant.toString().equalsIgnoreCase(raw)
                        || normalize(constant.name()).equals(normalizedInput)
                        || normalize(constant.toString()).equals(normalizedInput)) {
                    return constant;
                }
            }

            throw new IllegalArgumentException(
                    "Unknown " + targetType.getSimpleName() + " value '" + source + "'.");
        }

        private String normalize(String value) {
            return value.trim()
                    .replace('-', '_')
                    .replace(' ', '_')
                    .replaceAll("[^A-Za-z0-9_]", "")
                    .toUpperCase();
        }
    }
}
