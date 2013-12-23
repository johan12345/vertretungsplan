package com.johan.vertretungsplan;

import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.widget.TextView;

import com.inscription.ChangeLogDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;

public class LinkAlertDialog {

	 public static AlertDialog create(final Context context, String title, String text) {
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
	   .setPositiveButton("OK", null)
	   .setNegativeButton("Changelog", new DialogInterface.OnClickListener() {
	    			           public void onClick(DialogInterface dialog, int id) {
	    			        	 //Launch change log dialog
	    				    		ChangeLogDialog _ChangelogDialog = new ChangeLogDialog(context); 
	    				    		_ChangelogDialog.show();  
	    			           }
	    			       })
	   .setView(message)
	   .create();
	 }
	}