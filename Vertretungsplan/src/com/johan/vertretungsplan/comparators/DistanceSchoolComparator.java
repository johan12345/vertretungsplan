package com.johan.vertretungsplan.comparators;

import java.util.Comparator;

import com.johan.vertretungsplan.objects.Schule;

public class DistanceSchoolComparator implements Comparator<Schule> {

	@Override
	public int compare(Schule one, Schule two) {
		return Double.compare(one.getDistance(), two.getDistance());
	}

}
