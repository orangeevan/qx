package com.haipaite.common.resource.other;

import java.util.Comparator;

public interface IndexGetter {
    String getName();

    boolean isUnique();

    Object getValue(Object paramObject);

    Comparator getComparator();

    boolean hasComparator();
}