package practice.bookrentalapp.model.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import practice.bookrentalapp.validators.annotations.AtLeastOneNotNull;

import java.util.List;

@Data
public class BookSearchRequest {
    @NotNull
    @NotEmpty(message = "At least one search group must be provided.")
    @Valid
    List<SearchGroup> searchGroups;
    private Integer copies = 3;

    @Data
    @AtLeastOneNotNull(fieldNames = {"author", "title", "isbn", "category"}, message = "Please provide at least one search criteria")
    public static class SearchGroup {
        private String author;
        private String title;
        @Pattern(regexp = "^(?=(?:\\D*\\d){10}(?:(?:\\D*\\d){3})?$)[\\d-]+$", message = "Invalid ISBN format")
        private String isbn;
        private String category;
        private boolean exactMatch = false;
    }

}
