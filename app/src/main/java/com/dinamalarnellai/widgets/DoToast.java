/**
 * @category   JobsQuench
 * @package    com.contus.widgets
 * @version    1.0
 * @author     Sivakumar<sivakumar.b@contus.in>
 * @copyright  Copyright (C) 2014 <Contus> . All rights reserved. 
 * @license    http://www.apache.org/licenses/LICENSE-2.0
 */
package com.dinamalarnellai.widgets;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import app.dinamalarnellai.com.news.R;


/**
 * The Class DoToast is used to show the custom toast.. its having two types.
 * one is for sucess and another one is for failure message...
 */
public class DoToast extends Toast {

	/** The mi animation. */
	private Animation miAnimation;

	/** The layout. */
	private View layout;

	/** The Constant TYPE_CONFIRM. */
	public static final int TYPE_CONFIRM = 1;

	/** The Constant TYPE_ALERT. */
	public static final int TYPE_ALERT = 3;

	/**
	 * Instantiates a new do toast.
	 * 
	 * @param context
	 *            the context
	 * @param text
	 *            the text
	 * @param toastType
	 *            the toast type
	 */
	public DoToast(Context context, CharSequence text, int toastType) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layout = inflater.inflate(R.layout.custom_toast, null);
		layout.setLayoutParams(new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT));
		TextView textView = (TextView) layout.findViewById(R.id.text);
		if (toastType == TYPE_ALERT)
			textView.setBackgroundResource(R.drawable.toast_error_bg);
		else
			textView.setBackgroundResource(R.drawable.toast_confirm_bg);
		textView.setText(text);
		DoToast.this.setDuration(Toast.LENGTH_LONG);
		DoToast.this.setView(layout);
		DoToast.this.setGravity(Gravity.TOP | Gravity.FILL_HORIZONTAL, 0, 0);
		DoToast.this.show();

		miAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_down);
		miAnimation.setDuration(500);
		textView.setAnimation(miAnimation);
		textView.animate();
		miAnimation.start();

	}

	

}
