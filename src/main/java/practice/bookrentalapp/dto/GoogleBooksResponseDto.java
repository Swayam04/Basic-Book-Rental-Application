package practice.bookrentalapp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleBooksResponseDto {
    private int totalItems;
    private List<Item> items;

    @Data
    public static class Item {
        private VolumeInfo volumeInfo;
    }

    @Data
    public static class VolumeInfo {
        private String title;
        private List<String> authors;
        private String publisher;
        private String publishedDate;
        private List<IndustryIdentifier> industryIdentifiers;
        private int pageCount;
        private String printType;
        private List<String> categories;
        private double averageRating;
        private String maturityRating;
        private String language;

        @Data
        public static class IndustryIdentifier {
            private String type;
            private String identifier;
        }
    }
}
