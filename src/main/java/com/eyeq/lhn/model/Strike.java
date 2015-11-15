package com.eyeq.lhn.model;

import java.io.Serializable;

/**
 * @author Hana Lee
 * @since 2015-11-12 19:53
 */
public class Strike implements Serializable {

	private static final long serialVersionUID = -6655961542211180764L;

	private int count;

	public Strike(int count) {
		this.count = count;
	}

	public int getCount() {
		return count;
	}
}