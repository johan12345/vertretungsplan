package com.johan.vertretungsplan.comparators;

import java.util.Comparator;

import com.johan.vertretungsplan.objects.Schule;

public class AlphabeticalSchoolComparator implements Comparator<Schule> {

	@Override
	public int compare(Schule one, Schule two) {
		int compare = one.getCity().compareTo(two.getCity());
		if(compare == 0) {
			compare = one.getName().compareTo(two.getName());
		}
		return compare;
	}

}
