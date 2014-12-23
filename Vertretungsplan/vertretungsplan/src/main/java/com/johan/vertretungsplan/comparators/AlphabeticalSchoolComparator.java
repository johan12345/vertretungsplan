package com.johan.vertretungsplan.comparators;

import com.johan.vertretungsplan.objects.Schule;

import java.util.Comparator;

public class AlphabeticalSchoolComparator implements Comparator<Schule> {

    @Override
    public int compare(Schule one, Schule two) {
        int compare = one.getCity().compareTo(two.getCity());
        if (compare == 0) {
            compare = one.getName().compareTo(two.getName());
        }
        return compare;
    }

}
