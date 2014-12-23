package com.johan.vertretungsplan.comparators;

import com.johan.vertretungsplan.objects.Schule;

import java.util.Comparator;

public class DistanceSchoolComparator implements Comparator<Schule> {

    @Override
    public int compare(Schule one, Schule two) {
        return Double.compare(one.getDistance(), two.getDistance());
    }

}
