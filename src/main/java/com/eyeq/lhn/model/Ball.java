package com.eyeq.lhn.model;

import java.io.Serializable;

/**
 * @author Hana Lee
 * @since 2015-11-12 19:53
 */
public class Ball implements Serializable {

	private static final long serialVersionUID = 3062084408012685339L;

	private int count;

	public Ball(int count) {
		this.count = count;
	}

	public int getCount() {
		return count;
	}
}