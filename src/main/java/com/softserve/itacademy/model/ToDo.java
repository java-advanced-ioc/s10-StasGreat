package com.softserve.itacademy.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "tasks")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ToDo {

    @EqualsAndHashCode.Include
    private User owner;
    private String title;
    private LocalDateTime createdAt;
    private List<Task> tasks = new ArrayList<>();

    @EqualsAndHashCode.Include
    public String getNormalizedTitle() {
        if (title == null) return null;
        String trimmed = title.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
