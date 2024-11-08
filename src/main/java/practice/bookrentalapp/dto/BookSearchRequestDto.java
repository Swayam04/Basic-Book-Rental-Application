package practice.bookrentalapp.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class BookSearchRequestDto {
    @NotNull
    @NotEmpty(message = "At least one search group must be provided.")
    List<SearchGroup> searchGroups;
    private Integer copies = 3;

    @Data
    public static class SearchGroup {
        private String author;
        private String title;
        private String isbn;
        private String category;
        private boolean exactMatch = false;

        @AssertTrue(message = "At least one parameter must be non null in search group")
        public boolean isValid() {
            return author != null || title != null || isbn != null || category != null;
        }
    }

}
