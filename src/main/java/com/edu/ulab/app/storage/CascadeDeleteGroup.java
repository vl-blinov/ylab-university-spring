package com.edu.ulab.app.storage;

import lombok.Data;
import java.util.List;

@Data
public class CascadeDeleteGroup {
    private Class<?> elementClass;
    private List<Long> elementIds;
}
