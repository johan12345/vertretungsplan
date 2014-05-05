package com.johan.vertretungsplan.objects;

import java.util.Date;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Serialize;

@Entity
public class SavedVertretungsplan {
	@Id public String school_id;
	@Serialize public Vertretungsplan vertretungsplan;
	public Date date;
}
