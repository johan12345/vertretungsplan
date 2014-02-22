/*  LS Vertretungsplan - Android-App für den Vertretungsplan der Lornsenschule Schleswig
    Copyright (C) 2014  Johan v. Forstner

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see [http://www.gnu.org/licenses/]. */

package com.johan.vertretungsplan.ui;

import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.AlertDialog.Builder;
import org.holoeverywhere.widget.TextView;

import com.inscription.ChangeLogDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;

public class LinkAlertDialog {

	 public static Builder create (final Context context, String title, String text) {
	  final TextView message = new TextView(context);
	  message.setTextAppearance(context, android.R.style.TextAppearance_Medium);
	  
	  float scale = context.getResources().getDisplayMetrics().density;
	  int dpAsPixels = (int) (8*scale + 0.5f);
	  message.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);
	  // i.e.: R.string.dialog_message =>
	            // "Test this dialog following the link to dtmilano.blogspot.com"
	  Spanned s = Html.fromHtml(text);
	  message.setText(s);
	  message.setMovementMethod(LinkMovementMethod.getInstance());

	  return new AlertDialog.Builder(context)
	   .setTitle(title)
	   .setCancelable(true)
	   .setView(message);
	 }
	}